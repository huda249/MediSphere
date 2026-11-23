
import { useCallback, useEffect, useMemo, useState } from 'react'
import {
  Activity, AlertCircle, Clock3, RefreshCw,
  Search, Users, CheckCircle2
} from 'lucide-react'
import './EmergencyQueue.css'

const priorityNames = {
  1: 'Highest',
  2: 'High',
  3: 'Medium',
  4: 'Low',
  5: 'Lowest',
}

function EmergencyQueue() {
  const [queue, setQueue] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const [message, setMessage] = useState('')
  const [search, setSearch] = useState('')
  const [updatingId, setUpdatingId] = useState(null)

  const loadQueue = useCallback(async () => {
    setLoading(true)
    setError('')

    try {
      const response = await fetch('/api/emergency/queue')

      if (!response.ok) {
        throw new Error(`HTTP ${response.status}`)
      }

      const data = await response.json()

      if (!Array.isArray(data)) {
        throw new Error('Unexpected response from emergency service')
      }

      setQueue(data)
    } catch (err) {
      setError(
        `Could not load the emergency queue. Check the Java backend on port 8080. ${err.message}`
      )
    } finally {
      setLoading(false)
    }
  }, [])

  useEffect(() => {
    loadQueue()
  }, [loadQueue])

  // The API already returns patients in the correct priority order.
  const patients = useMemo(() => {
    return queue.map((patient, index) => ({
      id: patient.patientID ?? patient.id,
      name: patient.patientName ?? patient.name ?? 'Unknown',
      age: patient.patientAge ?? patient.age,
      ailment: patient.patientAilment ?? patient.ailment ?? '—',
      symptoms: patient.patientSymptoms ?? patient.symptoms ?? '—',
      priority: patient.patientPriority ?? patient.priority,
      status: patient.patientStatus ?? patient.status ?? 'WAITING',
      arrivalTime: patient.arrivalTime,
      position: index + 1,
    }))
  }, [queue])

  const filteredPatients = patients.filter(patient => {
    const term = search.trim().toLowerCase()
    return (
      patient.name.toLowerCase().includes(term) ||
      String(patient.id).includes(term) ||
      patient.ailment.toLowerCase().includes(term)
    )
  })

  const highestPriorityCount = patients.filter(
    patient => Number(patient.priority) === 1
  ).length

  const highPriorityCount = patients.filter(
    patient => Number(patient.priority) <= 2
  ).length

  const updatePriority = async (patientId, newPriority) => {
    setUpdatingId(patientId)
    setError('')
    setMessage('')

    try {
      const response = await fetch(
        `/api/emergency/priority/${patientId}/${newPriority}`,
        { method: 'PUT' }
      )

      if (!response.ok) {
        throw new Error(`HTTP ${response.status}`)
      }

      const success = await response.json()

      if (success !== true) {
        throw new Error('The backend rejected this priority change.')
      }

      await loadQueue()
      setMessage(`Priority updated for patient #${patientId}.`)
    } catch (err) {
      setError(`Could not update priority: ${err.message}`)
    } finally {
      setUpdatingId(null)
    }
  }

  const formatArrival = value => {
    if (!value) return '—'

    const date = new Date(value)

    if (Number.isNaN(date.getTime())) {
      return String(value)
    }

    return date.toLocaleString('en-CA', {
      month: 'short',
      day: 'numeric',
      hour: 'numeric',
      minute: '2-digit',
    })
  }

  return (
    <div className="emergency-page">
      <div className="emergency-hero">
        <div>
          <div className="eyebrow">MEDISPHERE / EMERGENCY QUEUE</div>
          <h1>Emergency<br />Queue</h1>
          <p>Priority-based patient care and real-time queue management.</p>
        </div>

        <button
          className="emergency-refresh"
          onClick={() => {
            setMessage('')
            loadQueue()
          }}
          disabled={loading}
        >
          <RefreshCw size={18} />
          Refresh Queue
        </button>
      </div>

      <div className="emergency-stats">
        <article className="emergency-stat emergency-sage">
          <div className="emergency-stat-top">
            <span>Waiting Patients</span>
            <Users size={21} />
          </div>
          <strong>{patients.length}</strong>
          <small>Currently in the emergency queue</small>
        </article>

        <article className="emergency-stat emergency-pink">
          <div className="emergency-stat-top">
            <span>High Priority (1–2)</span>
            <Activity size={21} />
          </div>
          <strong>{highPriorityCount}</strong>
          <small>Patients requiring earlier attention</small>
        </article>

        <article className="emergency-stat emergency-yellow">
          <div className="emergency-stat-top">
            <span>Priority 1</span>
            <AlertCircle size={21} />
          </div>
          <strong>{highestPriorityCount}</strong>
          <small>Highest priority category</small>
        </article>
      </div>

      {error && (
        <div className="emergency-alert emergency-error" role="alert">
          <AlertCircle size={18} />
          <span>{error}</span>
        </div>
      )}

      {message && (
        <div className="emergency-alert emergency-success" role="status">
          <CheckCircle2 size={18} />
          <span>{message}</span>
        </div>
      )}

      <section className="emergency-panel">
        <div className="emergency-panel-header">
          <div>
            <h2>Live Priority Queue</h2>
            <p>
              Sorted by priority, then arrival time.
              Position 1 is first in line.
            </p>
          </div>

          <span className="emergency-count">
            {patients.length} waiting
          </span>
        </div>

        <div className="emergency-toolbar">
          <div className="emergency-search">
            <Search size={18} />
            <input
              value={search}
              onChange={event => setSearch(event.target.value)}
              type="search"
              placeholder="Search patient name, ID, or condition..."
              aria-label="Search emergency patients"
            />
          </div>

          <span>{filteredPatients.length} results</span>
        </div>

        <div className="emergency-table-wrap">
          <table className="emergency-table">
            <thead>
              <tr>
                <th>Position</th>
                <th>Patient</th>
                <th>Condition</th>
                <th>Arrival</th>
                <th>Priority</th>
                <th>Status</th>
              </tr>
            </thead>

            <tbody>
              {filteredPatients.map(patient => (
                <tr key={patient.id}>
                  <td>
                    <span className="emergency-position">
                      #{patient.position}
                    </span>
                  </td>

                  <td>
                    <div className="emergency-patient-name">
                      {patient.name}
                    </div>
                    <span className="emergency-patient-meta">
                      ID {patient.id} · Age {patient.age ?? '—'}
                    </span>
                  </td>

                  <td>
                    <div>{patient.ailment}</div>
                    <span className="emergency-patient-meta">
                      {patient.symptoms}
                    </span>
                  </td>

                  <td>
                    <div className="emergency-arrival">
                      <Clock3 size={15} />
                      {formatArrival(patient.arrivalTime)}
                    </div>
                  </td>

                  <td>
                    <div className="emergency-priority-cell">
                      <span
                        className={`emergency-priority-dot priority-${patient.priority}`}
                      />

                      <select
                        value={patient.priority}
                        onChange={event =>
                          updatePriority(
                            patient.id,
                            Number(event.target.value)
                          )
                        }
                        disabled={updatingId !== null}
                        aria-label={`Change priority for ${patient.name}`}
                      >
                        {[1, 2, 3, 4, 5].map(level => (
                          <option key={level} value={level}>
                            {level} — {priorityNames[level]}
                          </option>
                        ))}
                      </select>
                    </div>
                  </td>

                  <td>
                    <span className="emergency-status">
                      {patient.status}
                    </span>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>

          {loading && (
            <div className="emergency-empty">
              Loading emergency queue...
            </div>
          )}

          {!loading && filteredPatients.length === 0 && (
            <div className="emergency-empty">
              <Users size={32} />
              <h3>No patients found</h3>
              <p>
                {search
                  ? 'Try another search term.'
                  : 'No patients are currently waiting.'}
              </p>
            </div>
          )}
        </div>
      </section>

      <div className="emergency-note">
        <AlertCircle size={18} />
        <p>
          Priority 1 is the highest and priority 5 is the lowest.
          Patients with the same priority are ordered by arrival time.
          This is a demonstration of the team's numeric queue rules,
          not a clinical triage recommendation.
        </p>
      </div>
    </div>
  )
}

export default EmergencyQueue
