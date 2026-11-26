
import { useCallback, useEffect, useState } from 'react'
import {
  Droplets, HeartPulse, Users, RefreshCw,
  Plus, Search, AlertCircle, CheckCircle2
} from 'lucide-react'
import './Donors.css'

const BLOOD_TYPES = [
  'O-', 'O+', 'A-', 'A+',
  'B-', 'B+', 'AB-', 'AB+'
]

const emptyDonor = {
  name: '',
  bloodType: 'O+',
  address: '',
}

const emptyBlood = {
  bloodType: 'O+',
  quantityUnits: '1',
  donorId: '',
}

const emptyOrgan = {
  organType: 'Kidney',
  bloodType: 'O+',
}

async function apiRequest(url, options) {
  const response = await fetch(url, options)
  const text = await response.text()

  if (!response.ok) {
    throw new Error(
      `Request failed (${response.status}): ${text.slice(0, 180)}`
    )
  }

  if (!text) return null

  try {
    return JSON.parse(text)
  } catch {
    return text
  }
}

const jsonOptions = body => ({
  method: 'POST',
  headers: { 'Content-Type': 'application/json' },
  body: JSON.stringify(body),
})

function Donors() {
  const [donors, setDonors] = useState([])
  const [blood, setBlood] = useState([])
  const [organs, setOrgans] = useState([])
  const [loading, setLoading] = useState(true)
  const [working, setWorking] = useState(false)
  const [error, setError] = useState('')
  const [success, setSuccess] = useState('')
  const [activeTab, setActiveTab] = useState('donors')

  const [donorForm, setDonorForm] = useState(emptyDonor)
  const [bloodForm, setBloodForm] = useState(emptyBlood)
  const [organForm, setOrganForm] = useState(emptyOrgan)

  const [matchType, setMatchType] = useState('blood')
  const [matchBloodType, setMatchBloodType] = useState('O+')
  const [matchOrganType, setMatchOrganType] = useState('Kidney')
  const [matchUnits, setMatchUnits] = useState('1')
  const [matchResult, setMatchResult] = useState(null)

  const loadData = useCallback(async () => {
    setLoading(true)
    setError('')

    try {
      const [donorData, bloodData, organData] = await Promise.all([
        apiRequest('/api/donors'),
        apiRequest('/api/blood-bank/inventory'),
        apiRequest('/api/organs'),
      ])

      if (
        !Array.isArray(donorData) ||
        !Array.isArray(bloodData) ||
        !Array.isArray(organData)
      ) {
        throw new Error('Unexpected inventory response format')
      }

      setDonors(donorData)
      setBlood(bloodData)
      setOrgans(organData)
    } catch (err) {
      setError(
        `Unable to load donor data. Check the backend on port 8083. ${err.message}`
      )
    } finally {
      setLoading(false)
    }
  }, [])

  useEffect(() => {
    loadData()
  }, [loadData])

  const runAction = async (action, message) => {
    setWorking(true)
    setError('')
    setSuccess('')

    try {
      const result = await action()
      await loadData()
      setSuccess(
        typeof result === 'string' && result.trim()
          ? `${message} Backend response: ${result}`
          : message
      )
      return true
    } catch (err) {
      setError(err.message)
      return false
    } finally {
      setWorking(false)
    }
  }

const registerDonor = async event => {
  event.preventDefault()

    if (!donorForm.name.trim() || !donorForm.address.trim()) {
      setError('Donor name and address are required.')
      return
    }

    const payload = {
      name: donorForm.name.trim(),
      bloodType: donorForm.bloodType,
      address: donorForm.address.trim(),
    }

    const saved = await runAction(
      async () => {
        const result = await apiRequest(
          '/api/donors/register',
          jsonOptions(payload)
        )

        if (!result || !result.id) {
          throw new Error('Donor registration failed.')
        }

        return result
      },
      'Donor registered successfully.'
    )

    if (saved) setDonorForm(emptyDonor)
  }

  const addBlood = async event => {
    event.preventDefault()

    const units = Number(bloodForm.quantityUnits)
    if (!Number.isInteger(units) || units <= 0) {
      setError('Blood units must be a positive whole number.')
      return
    }

    const payload = {
      bloodType: bloodForm.bloodType,
      quantityUnits: units,
      donorId: bloodForm.donorId
        ? Number(bloodForm.donorId)
        : null,
    }

    const saved = await runAction(
      () => apiRequest(
        '/api/blood-bank/add',
        jsonOptions(payload)
      ),
      'Blood inventory record added.'
    )

    if (saved) setBloodForm(emptyBlood)
  }

  const addOrgan = async event => {
    event.preventDefault()

    const saved = await runAction(
      () => apiRequest(
        '/api/organs/add',
        jsonOptions(organForm)
      ),
      'Organ resource added.'
    )

    if (saved) setOrganForm(emptyOrgan)
  }

  const findMatch = async event => {
    event.preventDefault()
    setWorking(true)
    setError('')
    setMatchResult(null)

    try {
      let url

      if (matchType === 'blood') {
        const units = Number(matchUnits)

        if (!Number.isInteger(units) || units <= 0) {
          throw new Error('Enter a positive number of units.')
        }

        url = `/api/match/blood?type=${
          encodeURIComponent(matchBloodType)
        }&units=${units}`
      } else {
        url = `/api/match/organ?organ=${
          encodeURIComponent(matchOrganType)
        }&bloodType=${encodeURIComponent(matchBloodType)}`
      }

      const result = await apiRequest(url)
      setMatchResult(result)
    } catch (err) {
      setError(`Matching request failed: ${err.message}`)
    } finally {
      setWorking(false)
    }
  }

  const reserveBlood = async item => {
    const unitsInput = window.prompt(
      `How many units should be reserved from blood stock #${item.id}?`,
      '1'
    )

    if (unitsInput === null) return

    const units = Number(unitsInput)

    if (!Number.isInteger(units) || units <= 0) {
      setError('Enter a positive whole number of units.')
      return
    }

    await runAction(
      () => apiRequest(
        `/api/reserve/blood/${item.id}?units=${units}`,
        { method: 'POST' }
      ),
      `Blood reservation request for stock #${item.id} completed.`
    )
  }

  const reserveOrgan = async item => {
    const confirmed = window.confirm(
      `Request reservation of ${item.organType} resource #${item.id}?`
    )

    if (!confirmed) return

    await runAction(
      () => apiRequest(
        `/api/reserve/organ/${item.id}`,
        { method: 'POST' }
      ),
      `Organ reservation request for resource #${item.id} completed.`
    )
  }

  const availableOrgans = organs.filter(
    item => item.status === 'AVAILABLE'
  ).length

  const availableDonors = donors.filter(
    item => item.available === true
  ).length

  const totalBloodUnits = blood.reduce(
    (sum, item) => sum + Number(item.quantityUnits || 0),
    0
  )

  return (
    <div className="donors-page">
      <div className="donors-hero">
        <div>
          <div className="eyebrow">
            MEDISPHERE / BLOOD & ORGAN DONORS
          </div>
          <h1>Blood & Organ<br />Donors</h1>
          <p>
            Manage donors, monitor inventory, and request
            compatible resources.
          </p>
        </div>

        <button
          className="donors-dark-button"
          onClick={loadData}
          disabled={loading || working}
        >
          <RefreshCw size={17} />
          Refresh
        </button>
      </div>

      <div className="donors-stats">
        <article className="donors-stat donors-sage">
          <div className="donors-stat-heading">
            <span>Available Donors</span>
            <Users size={21} />
          </div>
          <strong>{availableDonors}</strong>
          <small>{donors.length} total registered donors</small>
        </article>

        <article className="donors-stat donors-pink">
          <div className="donors-stat-heading">
            <span>Blood Units</span>
            <Droplets size={21} />
          </div>
          <strong>{totalBloodUnits}</strong>
          <small>Recorded in blood inventory</small>
        </article>

        <article className="donors-stat donors-blue">
          <div className="donors-stat-heading">
            <span>Available Organs</span>
            <HeartPulse size={21} />
          </div>
          <strong>{availableOrgans}</strong>
          <small>{organs.length} total organ records</small>
        </article>
      </div>

      {error && (
        <div className="donors-notice donors-error" role="alert">
          <AlertCircle size={18} />
          {error}
        </div>
      )}

      {success && (
        <div className="donors-notice donors-success" role="status">
          <CheckCircle2 size={18} />
          {success}
        </div>
      )}

      <section className="donors-panel donors-matching">
        <div className="donors-panel-header">
          <h2>Compatibility Search</h2>
          <p>Check potential matches using the Donor service</p>
        </div>

        <form className="donors-match-form" onSubmit={findMatch}>
          <label>
            Resource
            <select
              value={matchType}
              onChange={event => {
                setMatchType(event.target.value)
                setMatchResult(null)
              }}
            >
              <option value="blood">Blood</option>
              <option value="organ">Organ</option>
            </select>
          </label>

          {matchType === 'organ' && (
            <label>
              Organ type
              <select
                value={matchOrganType}
                onChange={event =>
                  setMatchOrganType(event.target.value)
                }
              >
                {['Kidney', 'Liver', 'Heart', 'Lung'].map(item => (
                  <option key={item}>{item}</option>
                ))}
              </select>
            </label>
          )}

          <label>
            Recipient blood type
            <select
              value={matchBloodType}
              onChange={event =>
                setMatchBloodType(event.target.value)
              }
            >
              {BLOOD_TYPES.map(item => (
                <option key={item}>{item}</option>
              ))}
            </select>
          </label>

          {matchType === 'blood' && (
            <label>
              Required units
              <input
                type="number"
                min="1"
                step="1"
                value={matchUnits}
                onChange={event =>
                  setMatchUnits(event.target.value)
                }
              />
            </label>
          )}

          <button
            type="submit"
            className="donors-dark-button"
            disabled={working || loading}
          >
            <Search size={17} />
            Find Matches
          </button>
        </form>

       
{matchResult !== null && (
  <div className="donors-match-result">
    <h3>Compatibility Results</h3>

    {matchType === 'blood' && (
      <>
        <div className="donors-result-stats">
          <div className="donors-result-stat donors-result-blue">
            <span>Recipient Blood Type</span>
            <strong>{matchResult.patientBloodType ?? matchBloodType}</strong>
          </div>

          <div className="donors-result-stat donors-result-yellow">
            <span>Available Blood Units</span>
            <strong>{matchResult.totalAvailableUnits ?? 0}</strong>
          </div>
        </div>

        <div className="donors-result-section">
          <h4>Compatible Donor Blood Types</h4>

          <div className="donors-blood-tags">
            {(matchResult.compatibleDonorTypes ?? []).map(type => (
              <span key={type} className="donors-blood-tag">
                {type}
              </span>
            ))}
          </div>
        </div>

        <div className={
          matchResult.hasEnoughStock
            ? 'donors-result-message donors-result-success'
            : 'donors-result-message donors-result-warning'
        }>
          {matchResult.hasEnoughStock
            ? 'Sufficient matching blood stock is available.'
            : 'Insufficient matching blood stock for the requested quantity.'}
        </div>

        {Array.isArray(matchResult.matchingInventory) &&
          matchResult.matchingInventory.length > 0 && (
          <div className="donors-result-section">
            <h4>Matching Inventory</h4>

            {matchResult.matchingInventory.map((item, index) => (
              <div key={item.id ?? index} className="donors-inventory-match">
                <span>{item.bloodType}</span>
                <strong>{item.quantityUnits} units</strong>
              </div>
            ))}
          </div>
        )}
      </>
    )}

   
{matchType === 'organ' && (
  <>
    <div className="donors-result-stats">
      <div className="donors-result-stat donors-result-blue">
        <span>Requested Organ</span>
        <strong>
          {matchResult.requestedOrgan ?? matchOrganType}
        </strong>
      </div>

      <div className="donors-result-stat donors-result-yellow">
        <span>Recipient Blood Type</span>
        <strong>
          {matchResult.patientBloodType ?? matchBloodType}
        </strong>
      </div>
    </div>

    <div className="donors-result-section">
      <h4>Compatible Donor Blood Types</h4>

      <div className="donors-blood-tags">
        {(matchResult.compatibleDonorTypes ?? []).map(type => (
          <span key={type} className="donors-blood-tag">
            {type}
          </span>
        ))}
      </div>
    </div>

    <div className="donors-result-stats">
      <div className="donors-result-stat donors-result-sage">
        <span>Registered Matching Donors</span>
        <strong>
          {matchResult.registeredLiveDonors?.length ?? 0}
        </strong>
      </div>

      <div className="donors-result-stat donors-result-pink">
        <span>Available Organs in Storage</span>
        <strong>
          {matchResult.availableOrgansInStorage?.length ?? 0}
        </strong>
      </div>
    </div>

    {Array.isArray(matchResult.registeredLiveDonors) &&
      matchResult.registeredLiveDonors.length > 0 && (
        <div className="donors-result-section">
          <h4>Matching Registered Donors</h4>

          {matchResult.registeredLiveDonors.map((donor, index) => (
            <div
              key={donor.id ?? index}
              className="donors-inventory-match"
            >
              <span>{donor.name ?? `Donor #${donor.id}`}</span>
              <strong>{donor.bloodType}</strong>
            </div>
          ))}
        </div>
      )}

    {Array.isArray(matchResult.availableOrgansInStorage) &&
      matchResult.availableOrgansInStorage.length > 0 && (
        <div className="donors-result-section">
          <h4>Matching Organ Inventory</h4>

          {matchResult.availableOrgansInStorage.map((organ, index) => (
            <div
              key={organ.id ?? index}
              className="donors-inventory-match"
            >
              <span>
                {organ.organType} #{organ.id}
              </span>
              <strong>{organ.bloodType}</strong>
            </div>
          ))}
        </div>
      )}

    {!(matchResult.registeredLiveDonors?.length > 0) &&
      !(matchResult.availableOrgansInStorage?.length > 0) && (
        <div className="donors-result-message donors-result-warning">
          No matching organ resources or registered donors
          are currently available.
        </div>
      )}
  </>
)}

  </div>
)}

      </section>

      <section className="donors-panel">
        <div className="donors-tabs">
          {[
            ['donors', 'Registered Donors'],
            ['blood', 'Blood Inventory'],
            ['organs', 'Organ Inventory'],
          ].map(([key, label]) => (
            <button
              key={key}
              className={activeTab === key ? 'selected' : ''}
              onClick={() => setActiveTab(key)}
            >
              {label}
            </button>
          ))}
        </div>

        {activeTab === 'donors' && (
          <>
            <div className="donors-panel-header">
              <h2>Donor Directory</h2>
              <p>Registered donor records from the live API</p>
            </div>

            <div className="donors-table-wrap">
              <table className="donors-table">
                <thead>
                  <tr>
                    <th>ID</th>
                    <th>Name</th>
                    <th>Blood Type</th>
                    <th>Address</th>
                    <th>Availability</th>
                  </tr>
                </thead>
                <tbody>
                  {donors.map(item => (
                    <tr key={item.id}>
                      <td>#{item.id}</td>
                      <td><strong>{item.name}</strong></td>
                      <td>{item.bloodType}</td>
                      <td>{item.address || 'Not provided'}</td>
                      <td>
                        <span className={`donors-pill ${
                          item.available
                            ? 'donors-available'
                            : 'donors-unavailable'
                        }`}>
                          {item.available ? 'Available' : 'Unavailable'}
                        </span>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
              {!loading && donors.length === 0 && (
                <p className="donors-empty">No donors registered yet.</p>
              )}
            </div>

            <form className="donors-add-form" onSubmit={registerDonor}>
              <h3><Plus size={17} /> Register Donor</h3>

              <div className="donors-form-grid">
                <label>
                  Full name
                  <input
                    required
                    value={donorForm.name}
                    onChange={event => setDonorForm({
                      ...donorForm,
                      name: event.target.value
                    })}
                    placeholder="Enter donor's full name"
                  />
                </label>

                <label>
                  Blood type
                  <select
                    value={donorForm.bloodType}
                    onChange={event => setDonorForm({
                      ...donorForm,
                    bloodType: event.target.value
                    })}
                  >
                    {BLOOD_TYPES.map(item => (
                      <option key={item}>{item}</option>
                    ))}
                  </select>
                </label>

                <label>
                  Address
                  <input
                    required
                    value={donorForm.address}
                    onChange={event => setDonorForm({
                      ...donorForm,
                      address: event.target.value
                    })}
                    placeholder="Enter donor's address"
                  />
                </label>
              </div>

              <button className="donors-dark-button" disabled={working}>
                <Plus size={17} />
                Register Donor
              </button>
            </form>
          </>
        )}

        {activeTab === 'blood' && (
          <>
            <div className="donors-panel-header">
              <h2>Blood Inventory</h2>
              <p>Blood stock available to the hospital</p>
            </div>

            <div className="donors-table-wrap">
              <table className="donors-table">
                <thead>
                  <tr>
                    <th>Stock ID</th>
                    <th>Blood Type</th>
                    <th>Units</th>
                    <th>Donation Date</th>
                    <th>Action</th>
                  </tr>
                </thead>
                <tbody>
                  {blood.map(item => (
                    <tr key={item.id}>
                      <td>#{item.id}</td>
                      <td><strong>{item.bloodType}</strong></td>
                      <td>{item.quantityUnits}</td>
                      <td>{item.donationDate || 'â€”'}</td>
                      <td>
                        <button
                          className="donors-action-button"
                          disabled={working || item.quantityUnits <= 0}
                          onClick={() => reserveBlood(item)}
                        >
                          Reserve
                        </button>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>

              {!loading && blood.length === 0 && (
                <p className="donors-empty">
                  No blood inventory records yet.
                </p>
              )}
            </div>

            <form className="donors-add-form" onSubmit={addBlood}>
              <h3><Plus size={17} /> Add Blood Stock</h3>

              <div className="donors-form-grid">
                <label>
                  Blood type
                  <select
                    value={bloodForm.bloodType}
                    onChange={event => setBloodForm({
                      ...bloodForm,
                      bloodType: event.target.value
                    })}
                  >
                    {BLOOD_TYPES.map(item => (
                      <option key={item}>{item}</option>
                    ))}
                  </select>
                </label>

                <label>
                  Quantity (units)
                  <input
                    required
                    type="number"
                    min="1"
                    step="1"
                    value={bloodForm.quantityUnits}
                    onChange={event => setBloodForm({
                      ...bloodForm,
                      quantityUnits: event.target.value
                    })}
                  />
                </label>

                <label>
                  Donor ID (optional)
                  <input
                    type="number"
                    min="1"
                    step="1"
                    value={bloodForm.donorId}
                    onChange={event => setBloodForm({
                      ...bloodForm,
                      donorId: event.target.value
                    })}
                    placeholder="Existing donor ID"
                  />
                </label>
              </div>

              <button className="donors-dark-button" disabled={working}>
                <Plus size={17} />
                Add Blood Stock
              </button>
            </form>
          </>
        )}

        {activeTab === 'organs' && (
          <>
            <div className="donors-panel-header">
              <h2>Organ Inventory</h2>
              <p>Organ resources and their reservation status</p>
            </div>

            <div className="donors-table-wrap">
              <table className="donors-table">
                <thead>
                  <tr>
                    <th>Resource ID</th>
                    <th>Organ</th>
                    <th>Blood Type</th>
                    <th>Status</th>
                    <th>Action</th>
                  </tr>
                </thead>
                <tbody>
                  {organs.map(item => (
                    <tr key={item.id}>
                      <td>#{item.id}</td>
                      <td><strong>{item.organType}</strong></td>
                      <td>{item.bloodType}</td>
                      <td>
                        <span className={`donors-pill ${
                          item.status === 'AVAILABLE'
                            ? 'donors-available'
                            : 'donors-unavailable'
                        }`}>
                          {item.status}
                        </span>
                      </td>
                      <td>
                        <button
                          className="donors-action-button"
                          disabled={
                            working || item.status !== 'AVAILABLE'
                          }
                          onClick={() => reserveOrgan(item)}
                        >
                          Reserve
                        </button>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>

              {!loading && organs.length === 0 && (
                <p className="donors-empty">
                  No organ inventory records yet.
                </p>
              )}
            </div>

            <form className="donors-add-form" onSubmit={addOrgan}>
              <h3><Plus size={17} /> Add Organ Resource</h3>

              <div className="donors-form-grid">
                <label>
                  Organ type
                  <select
                    value={organForm.organType}
                    onChange={event => setOrganForm({
                      ...organForm,
                      organType: event.target.value
                    })}
                  >
                    {['Kidney', 'Liver', 'Heart', 'Lung']
                      .map(item => (
                        <option key={item}>{item}</option>
                      ))}
                  </select>
                </label>

                <label>
                  Blood type
                  <select
                    value={organForm.bloodType}
                    onChange={event => setOrganForm({
                      ...organForm,
                      bloodType: event.target.value
                    })}
                  >
                    {BLOOD_TYPES.map(item => (
                      <option key={item}>{item}</option>
                    ))}
                  </select>
                </label>
              </div>

              <button className="donors-dark-button" disabled={working}>
                <Plus size={17} />
                Add Organ
              </button>
            </form>
          </>
        )}
      </section>

      <p className="donors-disclaimer">
  Compatibility results are generated by the hospital's
  donor matching service and require clinical verification.
</p>
    </div>
  )
}

export default Donors
