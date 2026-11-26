
import { useCallback, useEffect, useMemo, useState } from 'react'
import {
  BedDouble, Users, CheckCircle2, RefreshCw,
  Wrench, AlertCircle, ArrowRightLeft
} from 'lucide-react'
import './BedsEquipment.css'

function BedsEquipment() {
  const [patients, setPatients] = useState([])
  const [availableRooms, setAvailableRooms] = useState([])
  const [equipment, setEquipment] = useState([])
  const [roomsLoaded, setRoomsLoaded] = useState(false)
  const [equipmentError, setEquipmentError] = useState('')
  const [loading, setLoading] = useState(true)
  const [working, setWorking] = useState(false)
  const [error, setError] = useState('')
  const [success, setSuccess] = useState('')
  const [selectedPatient, setSelectedPatient] = useState('')
  const [selectedRoom, setSelectedRoom] = useState('')

  const loadPatients = useCallback(async () => {
    setLoading(true)
    setRoomsLoaded(false)
    setAvailableRooms([])
    setSelectedRoom('')
    setEquipmentError('')

    try {
      const response = await fetch('/api/scheduling/resources')

      if (!response.ok) {
        throw new Error(`HTTP ${response.status}`)
      }

      const resources = await response.json()

      if (!resources.equipment || typeof resources.equipment !== 'object' || Array.isArray(resources.equipment)) {
        throw new Error('Unexpected equipment response.')
      }

      const equipmentList = Object.entries(resources.equipment)
        .map(([id, status]) => ({
          id,
          name: id,
          status,
        }))
        .sort((a, b) => a.id.localeCompare(b.id))

      setEquipment(equipmentList)
    } catch (err) {
      setEquipment([])
      setEquipmentError(`Cannot load equipment: ${err.message}`)
    }
    setError('')

    try {
      const [patientResponse, roomResponse] = await Promise.all([
        fetch('/api/patients'),
        fetch('/api/admissions/rooms/available'),
      ])

      if (!patientResponse.ok || !roomResponse.ok) {
        throw new Error('Unable to retrieve patient or room records.')
      }

      const patientData = await patientResponse.json()
      const roomData = await roomResponse.json()

      if (!Array.isArray(patientData) || !Array.isArray(roomData)) {
        throw new Error('Unexpected hospital service response.')
      }

      setPatients(patientData)
      setAvailableRooms(roomData)
      setRoomsLoaded(true)
    } catch (err) {
      setError(`Cannot load hospital records: ${err.message}`)
    } finally {
      setLoading(false)
    }
  }, [])

  useEffect(() => {
    loadPatients()
  }, [loadPatients])

  const normalizedPatients = useMemo(() => {
    return patients.map(patient => ({
      id: patient.patientID ?? patient.id,
      name: patient.patientName ?? patient.name ?? 'Unknown',
      status: patient.patientStatus ?? patient.status ?? 'Unknown',
      roomNum: patient.roomNum ?? null,
      ailment: patient.patientAilment ?? patient.ailment ?? '—',
    }))
  }, [patients])

  const admittedPatients = normalizedPatients.filter(
    patient => patient.status === 'ADMITTED'
  )

  const waitingPatients = normalizedPatients.filter(
    patient => patient.status === 'WAITING'
  )

  const roomNumbers = [...new Set([
    ...availableRooms.map(room => Number(room.name)),
    ...admittedPatients
      .filter(patient => patient.roomNum !== null)
      .map(patient => Number(patient.roomNum)),
  ])].sort((a, b) => a - b)

  const rooms = roomNumbers.map(number => ({
    number,
    available: roomsLoaded && availableRooms.some(
      room => Number(room.name) === number && room.free
    ),
    patient: admittedPatients.find(
      patient => Number(patient.roomNum) === number
    ) ?? null,
  }))

  const occupiedCount = rooms.filter(room => room.patient).length
  const availableCount = availableRooms.filter(room => room.free).length

  const handleAdmission = async event => {
    event.preventDefault()
    setError('')
    setSuccess('')

    if (!selectedPatient || !selectedRoom) {
      setError('Please select both a patient and a room.')
      return
    }

    setWorking(true)

    try {
      const response = await fetch(
        `/api/admissions/admit/${selectedPatient}/${selectedRoom}`,
        { method: 'PUT' }
      )

      if (!response.ok) {
        throw new Error(`HTTP ${response.status}`)
      }

      const result = await response.json()

      if (result !== true) {
        throw new Error(
          'Admission rejected. The patient may not be waiting, or the room may already be reserved.'
        )
      }

      setSelectedPatient('')
      setSelectedRoom('')
      await loadPatients()
      setSuccess('Patient admitted successfully.')
    } catch (err) {
      setError(err.message)
    } finally {
      setWorking(false)
    }
  }

  const handleDischarge = async patient => {
    const confirmed = window.confirm(
      `Discharge ${patient.name} from room ${patient.roomNum}?`
    )

    if (!confirmed) return

    setWorking(true)
    setError('')
    setSuccess('')

    try {
      const response = await fetch(
        `/api/admissions/discharge/${patient.id}`,
        { method: 'PUT' }
      )

      if (!response.ok) {
        throw new Error(`HTTP ${response.status}`)
      }

      const result = await response.json()

      if (result !== true) {
        throw new Error(
          'Discharge rejected by the patient service.'
        )
      }

      await loadPatients()
      setSuccess(
        `${patient.name} was discharged. Room ${patient.roomNum} is available again.`
      )
    } catch (err) {
      setError(err.message)
    } finally {
      setWorking(false)
    }
  }

  return (
    <div className="beds-page">
      <div className="beds-hero">
        <div>
          <div className="eyebrow">
            MEDISPHERE / BEDS & EQUIPMENT
          </div>
          <h1>Beds &<br />Equipment</h1>
          <p>
            Manage hospital room assignments and monitor resources.
          </p>
        </div>

        <button
          className="beds-dark-button"
          onClick={() => {
            setSuccess('')
            loadPatients()
          }}
          disabled={loading || working}
        >
          <RefreshCw size={18} />
          Refresh
        </button>
      </div>

      <div className="beds-stats">
        <article className="beds-stat beds-sage">
          <div className="beds-stat-heading">
            <span>Available Rooms</span>
            <CheckCircle2 size={21} />
          </div>
          <strong>{roomsLoaded ? availableCount : "Unavailable"}</strong>
          <small>Retrieved from the admissions service</small>
        </article>

        <article className="beds-stat beds-pink">
          <div className="beds-stat-heading">
            <span>Occupied Rooms</span>
            <BedDouble size={21} />
          </div>
          <strong>{occupiedCount}</strong>
          <small>Currently assigned to patients</small>
        </article>

        <article className="beds-stat beds-blue">
          <div className="beds-stat-heading">
            <span>Waiting Patients</span>
            <Users size={21} />
          </div>
          <strong>{waitingPatients.length}</strong>
          <small>Eligible for admission</small>
        </article>
      </div>

      {error && (
        <div className="beds-notice beds-error" role="alert">
          <AlertCircle size={18} />
          {error}
        </div>
      )}

      {success && (
        <div className="beds-notice beds-success" role="status">
          <CheckCircle2 size={18} />
          {success}
        </div>
      )}

      <div className="beds-content-grid">
        <section className="beds-panel">
          <div className="beds-panel-header">
            <h2>Room Availability</h2>
            <p>Rooms currently tracked by the admissions service</p>
          </div>

          <div className="beds-room-list">
            {rooms.map(room => (
              <div className="beds-room-row" key={room.number}>
                <div className="beds-room-icon">
                  <BedDouble size={21} />
                </div>

                <div className="beds-room-info">
                  <strong>Room {room.number}</strong>
                  <span>
                    {room.patient
                      ? `${room.patient.name} · ID ${room.patient.id}`
                      : 'No patient assigned'}
                  </span>
                </div>

                <span className={
                  room.patient
                    ? 'beds-status beds-occupied'
                    : 'beds-status beds-available'
                }>
                  {room.patient ? 'Occupied' : (room.available ? 'Available' : 'Unavailable')}
                </span>
              </div>
            ))}
          </div>

          <p className="beds-footnote">
            Room availability is retrieved from the admissions service. The backend validates each reservation.
          </p>
        </section>

        <section className="beds-panel">
          <div className="beds-panel-header">
            <h2>Patient Admission</h2>
            <p>Assign a waiting patient to an available room</p>
          </div>

          <form className="beds-admission-form" onSubmit={handleAdmission}>
            <label>
              Select Patient
              <select
                value={selectedPatient}
                onChange={event =>
                  setSelectedPatient(event.target.value)
                }
                required
                disabled={loading || working}
              >
                <option value="">Choose a waiting patient</option>

                {waitingPatients.map(patient => (
                  <option key={patient.id} value={patient.id}>
                    #{patient.id} — {patient.name}
                  </option>
                ))}
              </select>
            </label>

            <label>
              Assign Room
              <select
                value={selectedRoom}
                onChange={event =>
                  setSelectedRoom(event.target.value)
                }
                required
                disabled={loading || working}
              >
                <option value="">Choose an available room</option>

                {rooms
                  .filter(room => room.available && !room.patient)
                  .map(room => (
                    <option key={room.number} value={room.number}>
                      Room {room.number}
                    </option>
                  ))}
              </select>
            </label>

            <button
              type="submit"
              className="beds-dark-button beds-submit"
              disabled={loading || working || !roomsLoaded || !availableCount || !waitingPatients.length}
            >
              <ArrowRightLeft size={18} />
              {working ? 'Processing...' : 'Admit Patient'}
            </button>
          </form>

          <div className="beds-form-note">
            A successful admission changes the patient's status
            from WAITING to ADMITTED and removes them from
            the Emergency Queue.
          </div>
        </section>
      </div>

      <section className="beds-panel beds-admissions-panel">
        <div className="beds-panel-header">
          <h2>Current Admissions</h2>
          <p>Patients currently assigned to hospital rooms</p>
        </div>

        <div className="beds-table-wrap">
          <table className="beds-table">
            <thead>
              <tr>
                <th>Patient ID</th>
                <th>Name</th>
                <th>Condition</th>
                <th>Room</th>
                <th>Status</th>
                <th>Action</th>
              </tr>
            </thead>

            <tbody>
              {admittedPatients.map(patient => (
                <tr key={patient.id}>
                  <td>#{patient.id}</td>
                  <td><strong>{patient.name}</strong></td>
                  <td>{patient.ailment}</td>
                  <td>{patient.roomNum ?? '—'}</td>
                  <td>
                    <span className="beds-status beds-occupied">
                      ADMITTED
                    </span>
                  </td>
                  <td>
                    <button
                      className="beds-discharge"
                      onClick={() => handleDischarge(patient)}
                      disabled={working}
                    >
                      Discharge
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>

          {!loading && admittedPatients.length === 0 && (
            <div className="beds-empty">
              No patients are currently admitted.
            </div>
          )}

          {loading && (
            <div className="beds-empty">Loading admissions...</div>
          )}
        </div>
      </section>

      <section className="beds-panel beds-equipment-panel">
        <div className="beds-panel-header beds-equipment-heading">
          <div>
            <h2>Equipment Status</h2>
            <p>Hospital equipment overview</p>
          </div>
          <span className="beds-demo-tag">Scheduling service</span>
        </div>

        <div className="beds-equipment-list">
          {equipmentError && (
            <div className="beds-notice beds-error">
              {equipmentError}
            </div>
          )}
          {equipment.map(item => (
            <div className="beds-equipment-row" key={item.id ?? item.name}>
              <div className="beds-equipment-name">
                <Wrench size={18} />
                <span>{item.name ?? item.id}</span>
              </div>

              <span className={
                item.status === 'READY'
                  ? 'beds-status beds-available'
                  : 'beds-status beds-maintenance'
              }>
                {item.status === 'READY' ? 'Operational' : item.status}
              </span>
            </div>
          ))}
        </div>

        <p className="beds-footnote">
          Equipment status is retrieved from the scheduling service.
        </p>
      </section>
    </div>
  )
}

export default BedsEquipment
