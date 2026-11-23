
import { useEffect, useState } from 'react'
import {
  CalendarDays, ChevronLeft, ChevronRight,
  RefreshCw, AlertCircle, CheckCircle2
} from 'lucide-react'
import './Scheduling.css'

const API = '/api/scheduling'

const startOfWeek = (date) => {
  const result = new Date(date)
  result.setHours(0, 0, 0, 0)
  result.setDate(result.getDate() - ((result.getDay() + 6) % 7))
  return result
}

const dateKey = (date) => {
  const y = date.getFullYear()
  const m = String(date.getMonth() + 1).padStart(2, '0')
  const d = String(date.getDate()).padStart(2, '0')
  return `${y}-${m}-${d}`
}

const formatTime = (date) =>
  new Date(date).toLocaleTimeString('en-CA', {
    hour: '2-digit',
    minute: '2-digit',
    hour12: false
  })

const initialForm = () => ({
  patientId: '',
  department: 'General',
  room: 'OR-1',
  bed: 'BED-1',
  equipment: 'EQ-1',
  date: dateKey(new Date()),
  start: '09:00',
  end: '10:00',
  recoveryEnd: '11:00'
})

export default function Scheduling() {
  const [week, setWeek] = useState(() => startOfWeek(new Date()))
  const [bookings, setBookings] = useState([])
  const [resources, setResources] = useState(null)
  const [department, setDepartment] = useState('All')
  const [roomFilter, setRoomFilter] = useState('All')
  const [form, setForm] = useState(initialForm)
  const [loading, setLoading] = useState(true)
  const [saving, setSaving] = useState(false)
  const [error, setError] = useState('')
  const [message, setMessage] = useState('')

  const loadData = async () => {
    setLoading(true)
    setError('')

    try {
      const [bookingResponse, resourceResponse] = await Promise.all([
        fetch(`${API}/bookings`),
        fetch(`${API}/resources`)
      ])

      if (!bookingResponse.ok || !resourceResponse.ok) {
        throw new Error('The scheduling service returned an error.')
      }

      setBookings(await bookingResponse.json())
      setResources(await resourceResponse.json())
    } catch {
      setError(
        'Cannot connect to the scheduling service. ' +
        'Check that the Java service is running on port 8082.'
      )
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    loadData()
  }, [])

  const weekDays = Array.from({ length: 7 }, (_, i) => {
    const date = new Date(week)
    date.setDate(week.getDate() + i)
    return date
  })

  const allRooms = Object.keys(resources?.rooms ?? {}).sort()
  const visibleRooms = allRooms.filter(room => {
    const matchesDepartment =
      department === 'All' || resources.rooms[room] === department
    const matchesRoom = roomFilter === 'All' || room === roomFilter
    return matchesDepartment && matchesRoom
  })

  const changeWeek = (amount) => {
    setWeek(current => {
      const next = new Date(current)
      next.setDate(next.getDate() + amount * 7)
      return next
    })
  }

  const updateForm = (field, value) => {
    setForm(current => ({
      ...current,
      [field]: value,
      ...(field === 'department'
        ? { room: value === 'Orthopedics' ? 'OR-3' : 'OR-1' }
        : {})
    }))
  }

  const submitBooking = async (event) => {
    event.preventDefault()
    setError('')
    setMessage('')

    const starts = new Date(`${form.date}T${form.start}`)
    const ends = new Date(`${form.date}T${form.end}`)
    const recoveryEnds = new Date(`${form.date}T${form.recoveryEnd}`)

    if (
      !Number.isInteger(Number(form.patientId)) ||
      Number(form.patientId) <= 0
    ) {
      setError('Enter a valid numeric patient ID.')
      return
    }

    if (
      !Number.isFinite(starts.getTime()) ||
      !(starts < ends && ends < recoveryEnds)
    ) {
      setError('Start time must be before end time, and recovery must finish later.')
      return
    }

    const payload = {
      patientId: Number(form.patientId),
      department: form.department,
      room: form.room,
      bed: form.bed,
      equipment: form.equipment,
      starts: starts.toISOString(),
      ends: ends.toISOString(),
      recoveryEnds: recoveryEnds.toISOString()
    }

    setSaving(true)

    try {
      const response = await fetch(`${API}/bookings`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(payload)
      })

      if (!response.ok) {
        if (response.status === 409) {
          throw new Error(
            'Scheduling conflict: the requested room, bed, ' +
            'equipment, or patient is unavailable.'
          )
        }
        if (response.status === 404) {
          throw new Error('Patient ID was not found in the patient service.')
        }
        if (response.status === 503) {
          throw new Error('The patient service is currently unavailable.')
        }
        throw new Error(`Reservation failed (HTTP ${response.status}).`)
      }

      setMessage('Operating room reservation created successfully.')
      setWeek(startOfWeek(starts))
      await loadData()
    } catch (err) {
      setError(err.message)
    } finally {
      setSaving(false)
    }
  }

  return (
    <div className="scheduling-page">
      <div className="schedule-heading">
        <div>
          <div className="eyebrow">MEDISPHERE / OR SCHEDULING</div>
          <h1>Operating Room Scheduling</h1>
          <p>Plan procedures, check availability, and manage operating rooms efficiently.</p>
        </div>
        <button className="schedule-refresh" onClick={loadData}>
          <RefreshCw size={17} /> Refresh
        </button>
      </div>

      {error && (
        <div className="schedule-alert error" role="alert">
          <AlertCircle size={18} /> {error}
        </div>
      )}

      {message && (
        <div className="schedule-alert success" role="status">
          <CheckCircle2 size={18} /> {message}
        </div>
      )}

      <div className="schedule-layout">
        <div className="schedule-main">
          <div className="schedule-controls">
            <label>
              Department
              <select value={department} onChange={e => {
                setDepartment(e.target.value)
                setRoomFilter('All')
              }}>
                <option value="All">All Departments</option>
                <option value="General">General</option>
                <option value="Orthopedics">Orthopedics</option>
              </select>
            </label>

            <label>
              Operating Room
              <select value={roomFilter} onChange={e => setRoomFilter(e.target.value)}>
                <option value="All">All Rooms</option>
                {allRooms
                  .filter(room => department === 'All' ||
                    resources.rooms[room] === department)
                  .map(room => <option key={room}>{room}</option>)}
              </select>
            </label>

            <div className="week-switcher">
              <button onClick={() => changeWeek(-1)} aria-label="Previous week">
                <ChevronLeft size={19} />
              </button>
              <span>
                {weekDays[0].toLocaleDateString('en-CA', {
                  month: 'short', day: 'numeric'
                })}
                {' – '}
                {weekDays[6].toLocaleDateString('en-CA', {
                  month: 'short', day: 'numeric', year: 'numeric'
                })}
              </span>
              <button onClick={() => changeWeek(1)} aria-label="Next week">
                <ChevronRight size={19} />
              </button>
            </div>

            <button className="schedule-today" onClick={() =>
              setWeek(startOfWeek(new Date()))
            }>
              Today
            </button>
          </div>

          <div className="schedule-calendar">
            <div className="schedule-grid">
              <div className="schedule-corner">ROOM</div>
              {weekDays.map(date => {
                const today = dateKey(date) === dateKey(new Date())
                return (
                  <div className={`schedule-day-head ${today ? 'current' : ''}`} key={dateKey(date)}>
                    <span>{date.toLocaleDateString('en-CA', { weekday: 'short' })}</span>
                    <strong>{date.getDate()}</strong>
                  </div>
                )
              })}

              {visibleRooms.map(room => (
                <div className="schedule-row" key={room}>
                  <div className="schedule-room">
                    <strong>{room}</strong>
                    <small>{resources.rooms[room]}</small>
                  </div>

                  {weekDays.map(date => {
                    const dayBookings = bookings.filter(booking =>
                      booking.room === room &&
                      dateKey(new Date(booking.starts)) === dateKey(date)
                    )

                    return (
                      <div className="schedule-cell" key={`${room}-${dateKey(date)}`}>
                        {dayBookings.map(booking => (
                          <div className="booking-chip" key={booking.id}>
                            <strong>P-{booking.patientId}</strong>
                            <span>{formatTime(booking.starts)}–{formatTime(booking.ends)}</span>
                            <small>{booking.equipment}</small>
                          </div>
                        ))}
                      </div>
                    )
                  })}
                </div>
              ))}
            </div>

            {!loading && !error && bookings.length === 0 && (
              <p className="schedule-empty">
                No reservations yet. Create one using the form.
              </p>
            )}

            {loading && <p className="schedule-empty">Loading schedule...</p>}
          </div>

          <div className="schedule-legend">
            <span><i className="legend-scheduled" /> Scheduled</span>
            <span><i className="legend-today" /> Today</span>
            <span><i className="legend-empty" /> Available slot*</span>
          </div>
          <p className="schedule-disclaimer">
            *Empty calendar cells are not guaranteed available. The backend checks
            room, patient, bed, and equipment conflicts before confirming a reservation.
          </p>
        </div>

        <aside className="reservation-panel">
          <div className="reservation-title">
            <CalendarDays size={19} />
            <h2>New OR Reservation</h2>
          </div>

          <form onSubmit={submitBooking} className="reservation-form">
            <label>
              Patient ID
              <input
                type="number"
                min="1"
                required
                placeholder="e.g. 1001"
                value={form.patientId}
                onChange={e => updateForm('patientId', e.target.value)}
              />
            </label>

            <label>
              Department
              <select value={form.department}
                onChange={e => updateForm('department', e.target.value)}>
                <option value="General">General</option>
                <option value="Orthopedics">Orthopedics</option>
              </select>
            </label>

            <label>
              Operating Room
              <select value={form.room} onChange={e => updateForm('room', e.target.value)}>
                {Object.entries(resources?.rooms ?? {
                  'OR-1': 'General',
                  'OR-2': 'General',
                  'OR-3': 'Orthopedics'
                })
                  .filter(([, dept]) => dept === form.department)
                  .map(([room]) => <option key={room}>{room}</option>)}
              </select>
            </label>

            <label>
              Equipment
              <select value={form.equipment}
                onChange={e => updateForm('equipment', e.target.value)}>
                {Object.entries(resources?.equipment ?? {
                  'EQ-1': 'READY',
                  'EQ-2': 'READY',
                  'EQ-3': 'MAINTENANCE'
                }).map(([id, status]) => (
                  <option key={id} value={id} disabled={status !== 'READY'}>
                    {id} — {status}
                  </option>
                ))}
              </select>
            </label>

            <label>
              Surgery Date
              <input type="date" required value={form.date}
                onChange={e => updateForm('date', e.target.value)} />
            </label>

            <div className="reservation-time-grid">
              <label>
                Start Time
                <input type="time" required value={form.start}
                  onChange={e => updateForm('start', e.target.value)} />
              </label>
              <label>
                End Time
                <input type="time" required value={form.end}
                  onChange={e => updateForm('end', e.target.value)} />
              </label>
            </div>

            <label>
              Recovery End Time
              <input type="time" required value={form.recoveryEnd}
                onChange={e => updateForm('recoveryEnd', e.target.value)} />
            </label>

            <label>
              Recovery Bed
              <select value={form.bed} onChange={e => updateForm('bed', e.target.value)}>
                {(resources?.beds ?? ['BED-1', 'BED-2']).map(bed => (
                  <option key={bed}>{bed}</option>
                ))}
              </select>
            </label>

            <button className="reservation-submit" type="submit"
              disabled={saving || loading || !resources}>
              {saving ? 'Saving Reservation...' : 'Create Reservation'}
            </button>

            <p className="reservation-note">
              Reservations are checked and saved by the Java scheduling service.
              The patient must exist in Patient Management.
            </p>
          </form>
        </aside>
      </div>
    </div>
  )
}
