
import { useCallback, useEffect, useMemo, useState } from 'react'
import {
  Users, Plus, RefreshCw, Search, X,
  ClipboardList, Activity
} from 'lucide-react'
import './Patients.css'

const emptyForm = {
  id: '',
  name: '',
  age: '',
  ailment: '',
  symptoms: '',
  priority: '2',
}

function Patients() {
  const [patients, setPatients] = useState([])
  const [loading, setLoading] = useState(true)
  const [saving, setSaving] = useState(false)
  const [error, setError] = useState('')
  const [success, setSuccess] = useState('')
  const [search, setSearch] = useState('')
  const [showForm, setShowForm] = useState(false)
  const [form, setForm] = useState(emptyForm)

  const loadPatients = useCallback(async () => {
    setLoading(true)
    setError('')

    try {
      const response = await fetch('/api/patients')

      if (!response.ok) {
        throw new Error(`Request failed (${response.status})`)
      }

      const data = await response.json()

      if (!Array.isArray(data)) {
        throw new Error('Unexpected response from patient service')
      }

      setPatients(data)
    } catch (err) {
      setError(
        `Unable to load patients. Check that the Java patient service is running on port 8080. ${err.message}`
      )
    } finally {
      setLoading(false)
    }
  }, [])

  useEffect(() => {
    loadPatients()
  }, [loadPatients])

  // Match the JSON property names produced by the Java getters.
  const normalizedPatients = useMemo(() => {
    return patients.map(patient => ({
      id: patient.patientID ?? patient.id,
      name: patient.patientName ?? patient.name ?? '',
      age: patient.patientAge ?? patient.age,
      ailment: patient.patientAilment ?? patient.ailment ?? '',
      symptoms: patient.patientSymptoms ?? patient.symptoms ?? '',
      priority: patient.patientPriority ?? patient.priority,
      status: patient.patientStatus ?? patient.status ?? 'Unknown',
      room: patient.roomNum ?? null,
      arrivalTime: patient.arrivalTime ?? null,
    }))
  }, [patients])

  const filteredPatients = normalizedPatients.filter(patient => {
    const query = search.trim().toLowerCase()

    return (
      String(patient.id ?? '').includes(query) ||
      patient.name.toLowerCase().includes(query) ||
      patient.ailment.toLowerCase().includes(query)
    )
  })

  const waitingCount = normalizedPatients.filter(
    patient => patient.status.toUpperCase() === 'WAITING'
  ).length

  const updateForm = (field, value) => {
    setForm(previous => ({ ...previous, [field]: value }))
  }

  const registerPatient = async event => {
    event.preventDefault()
    setError('')
    setSuccess('')

    const id = Number(form.id)
    const age = Number(form.age)
    const priority = Number(form.priority)

    if (!Number.isInteger(id) || id <= 0) {
      setError('Patient ID must be a positive whole number.')
      return
    }

    if (!Number.isInteger(age) || age < 0 || age > 130) {
      setError('Enter a valid patient age.')
      return
    }

    if (!Number.isInteger(priority) || priority < 0) {
      setError('Priority must be a non-negative whole number.')
      return
    }

    if (normalizedPatients.some(patient => Number(patient.id) === id)) {
      setError('A patient with this ID is already displayed in the list.')
      return
    }

    const newPatient = {
      id,
      name: form.name.trim(),
      age,
      ailment: form.ailment.trim(),
      symptoms: form.symptoms.trim(),
      priority,
    }

    setSaving(true)

    try {
      const response = await fetch('/api/patients', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(newPatient),
      })

      if (!response.ok) {
        throw new Error(`Registration request failed (${response.status})`)
      }

      const registered = await response.json()

      if (registered !== true) {
        throw new Error('Patient service did not accept the registration')
      }

      setShowForm(false)
      setForm(emptyForm)
      setSearch('')
      await loadPatients()
      setSuccess(`Patient ${newPatient.name} registered successfully.`)
    } catch (err) {
      setError(err.message)
    } finally {
      setSaving(false)
    }
  }

  const formatArrival = value => {
    if (!value) return '—'
    const date = new Date(value)
    return Number.isNaN(date.getTime())
      ? String(value)
      : date.toLocaleString('en-CA', {
          month: 'short',
          day: 'numeric',
          hour: 'numeric',
          minute: '2-digit',
        })
  }

  return (
    <div className="patients-page">
      <div className="patients-hero">
        <div>
          <div className="eyebrow">MEDISPHERE / PATIENTS</div>
          <h1>Patient<br />Management</h1>
          <p>Organized patient records. Connected hospital care.</p>
        </div>

        <button
          className="patients-add-btn"
          onClick={() => {
            setShowForm(true)
            setError('')
            setSuccess('')
          }}
        >
          <Plus size={19} />
          Register Patient
        </button>
      </div>

      <div className="patients-stats">
        <div className="patients-stat patients-stat-sage">
          <div className="patients-stat-label">
            <span>Total Patients</span>
            <Users size={21} />
          </div>
          <strong>{normalizedPatients.length}</strong>
          <small>Registered in patient service</small>
        </div>

        <div className="patients-stat patients-stat-pink">
          <div className="patients-stat-label">
            <span>Waiting Patients</span>
            <Activity size={21} />
          </div>
          <strong>{waitingCount}</strong>
          <small>Current waiting status</small>
        </div>

        <div className="patients-stat patients-stat-blue">
          <div className="patients-stat-label">
            <span>Patient Records</span>
            <ClipboardList size={21} />
          </div>
          <strong>{normalizedPatients.length}</strong>
          <small>Available through the API</small>
        </div>
      </div>

      {error && (
        <div className="patients-alert patients-alert-error" role="alert">
          {error}
        </div>
      )}

      {success && (
        <div className="patients-alert patients-alert-success" role="status">
          {success}
        </div>
      )}

      <section className="patients-panel">
        <div className="patients-panel-header">
          <div>
            <h2>Patient Directory</h2>
            <p>Live records from the Patient Management service</p>
          </div>

          <button
            className="patients-refresh-btn"
            onClick={loadPatients}
            disabled={loading}
          >
            <RefreshCw size={17} />
            Refresh
          </button>
        </div>

        <div className="patients-toolbar">
          <div className="patients-search">
            <Search size={18} />
            <input
              type="search"
              placeholder="Search name, ID, or condition..."
              value={search}
              onChange={event => setSearch(event.target.value)}
              aria-label="Search patient directory"
            />
          </div>
          <span>{filteredPatients.length} records</span>
        </div>

        <div className="patients-table-scroll">
          <table className="patients-table">
            <thead>
              <tr>
                <th>Patient ID</th>
                <th>Patient Name</th>
                <th>Age</th>
                <th>Condition</th>
                <th>Priority</th>
                <th>Status</th>
                <th>Arrival</th>
              </tr>
            </thead>
            <tbody>
              {filteredPatients.map(patient => (
                <tr key={patient.id}>
                  <td className="patients-id">
                    #{patient.id}
                  </td>
                  <td>
                    <span className="patients-name">
                      {patient.name || 'Unnamed patient'}
                    </span>
                  </td>
                  <td>{patient.age ?? '—'}</td>
                  <td>{patient.ailment || '—'}</td>
                  <td>
                    <span className="patients-priority">
                      {patient.priority ?? '—'}
                    </span>
                  </td>
                  <td>
                    <span
                      className={`patients-status ${
                        patient.status.toUpperCase() === 'WAITING'
                          ? 'patients-status-waiting'
                          : 'patients-status-other'
                      }`}
                    >
                      {patient.status}
                    </span>
                  </td>
                  <td>{formatArrival(patient.arrivalTime)}</td>
                </tr>
              ))}
            </tbody>
          </table>

          {!loading && filteredPatients.length === 0 && (
            <div className="patients-empty">
              <Users size={32} />
              <h3>No patients found</h3>
              <p>
                {search
                  ? 'Try a different search.'
                  : 'Register a patient to get started.'}
              </p>
            </div>
          )}

          {loading && (
            <div className="patients-empty">Loading patient records...</div>
          )}
        </div>
      </section>

      {showForm && (
        <div className="patients-modal-backdrop">
          <div
            className="patients-modal"
            role="dialog"
            aria-modal="true"
            aria-labelledby="patients-modal-title"
          >
            <div className="patients-modal-header">
              <div>
                <div className="eyebrow">NEW PATIENT</div>
                <h2 id="patients-modal-title">Register Patient</h2>
              </div>

              <button
                className="patients-close-btn"
                onClick={() => setShowForm(false)}
                aria-label="Close registration form"
                disabled={saving}
              >
                <X size={20} />
              </button>
            </div>

            <form onSubmit={registerPatient}>
              <div className="patients-form-grid">
                <label>
                  Patient ID
                  <input
                    type="number"
                    min="1"
                    step="1"
                    required
                    value={form.id}
                    onChange={event => updateForm('id', event.target.value)}
                    placeholder="e.g. 1002"
                  />
                </label>

                <label>
                  Age
                  <input
                    type="number"
                    min="0"
                    max="130"
                    step="1"
                    required
                    value={form.age}
                    onChange={event => updateForm('age', event.target.value)}
                    placeholder="e.g. 35"
                  />
                </label>

                <label className="patients-form-full">
                  Full Name
                  <input
                    required
                    maxLength="100"
                    value={form.name}
                    onChange={event => updateForm('name', event.target.value)}
                    placeholder="e.g. Jordan Lee"
                  />
                </label>

                <label className="patients-form-full">
                  Condition / Ailment
                  <input
                    required
                    maxLength="200"
                    value={form.ailment}
                    onChange={event => updateForm('ailment', event.target.value)}
                    placeholder="e.g. Fractured ankle"
                  />
                </label>

                <label className="patients-form-full">
                  Symptoms
                  <textarea
                    required
                    rows="3"
                    value={form.symptoms}
                    onChange={event => updateForm('symptoms', event.target.value)}
                    placeholder="Describe symptoms..."
                  />
                </label>

                <label className="patients-form-full">
                  Emergency Priority (numeric)
                  <input
                    type="number"
                    min="0"
                    step="1"
                    required
                    value={form.priority}
                    onChange={event =>
                      updateForm('priority', event.target.value)
                    }
                  />
                  <small>
                    Enter the priority value used by your team's backend.
                  </small>
                </label>
              </div>

              <div className="patients-modal-actions">
                <button
                  type="button"
                  className="patients-cancel-btn"
                  onClick={() => setShowForm(false)}
                  disabled={saving}
                >
                  Cancel
                </button>

                <button
                  type="submit"
                  className="patients-submit-btn"
                  disabled={saving}
                >
                  {saving ? 'Registering...' : 'Register Patient'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  )
}

export default Patients
