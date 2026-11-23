
import { useState } from 'react'
import {
  LayoutDashboard, CalendarDays, Bell, Users, BedDouble,
  Droplets, Search, ChevronLeft, ChevronRight, ChevronDown,
  Activity, Clock3, Settings, Server, Wrench
} from 'lucide-react'
import './App.css'
import Scheduling from './pages/Scheduling'
import Patients from './pages/Patients'
import EmergencyQueue from './pages/EmergencyQueue'
import BedsEquipment from './pages/BedsEquipment'
import Donors from './pages/Donors'

const menuItems = [
  { name: 'Dashboard', icon: LayoutDashboard },
  { name: 'OR Scheduling', icon: CalendarDays },
  { name: 'Emergency Queue', icon: Bell },
  { name: 'Patients', icon: Users },
  { name: 'Beds & Equipment', icon: BedDouble },
  { name: 'Blood & Organ Donors', icon: Droplets },
]

const statistics = [
  { label: 'Patients', value: '128', color: 'sage', icon: Users },
  { label: 'Emergency Queue', value: '18', color: 'pink', icon: Activity },
  { label: 'Operating Rooms', value: '8', color: 'blue', icon: CalendarDays },
  { label: 'Available Beds', value: '24', color: 'yellow', icon: BedDouble },
]

const equipment = [
  { name: 'Ventilators', status: 'Operational' },
  { name: 'Anesthesia Machines', status: 'Operational' },
  { name: 'Patient Monitors', status: 'Operational' },
  { name: 'Defibrillators', status: 'Operational' },
  { name: 'Infusion Pumps', status: 'Maintenance' },
]

const operations = [
  { time: '09:00', name: 'Knee Arthroscopy', room: 'OR 2', color: 'blue' },
  { time: '10:30', name: 'Appendectomy', room: 'OR 1', color: 'sage' },
  { time: '13:30', name: 'Spine Fusion', room: 'OR 3', color: 'pink' },
  { time: '15:00', name: 'CABG', room: 'OR 4', color: 'yellow' },
  { time: '16:30', name: 'Laparoscopy', room: 'OR 1', color: 'sage' },
]

function HospitalCalendar() {
  const [today] = useState(() => new Date())
  const [viewDate, setViewDate] = useState(
    () => new Date(new Date().getFullYear(), new Date().getMonth(), 1)
  )

  const year = viewDate.getFullYear()
  const month = viewDate.getMonth()
  const firstDay = new Date(year, month, 1).getDay()
  const offset = (firstDay + 6) % 7
  const daysInMonth = new Date(year, month + 1, 0).getDate()

  const calendarDays = [
    ...Array(offset).fill(null),
    ...Array.from({ length: daysInMonth }, (_, index) => index + 1),
  ]

  while (calendarDays.length % 7 !== 0) {
    calendarDays.push(null)
  }

  const changeMonth = (change) => {
    setViewDate(new Date(year, month + change, 1))
  }

  const isToday = (day) =>
    day === today.getDate() &&
    month === today.getMonth() &&
    year === today.getFullYear()

  return (
    <section className="panel calendar-panel">
      <div className="panel-header">
        <button
          className="icon-button"
          onClick={() => changeMonth(-1)}
          aria-label="Previous month"
        >
          <ChevronLeft size={19} />
        </button>

        <h3>
          {viewDate.toLocaleDateString('en-CA', {
            month: 'long',
            year: 'numeric',
          })}
        </h3>

        <button
          className="icon-button"
          onClick={() => changeMonth(1)}
          aria-label="Next month"
        >
          <ChevronRight size={19} />
        </button>
      </div>

      <div className="calendar-grid">
        {['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun'].map(day => (
          <span className="calendar-weekday" key={day}>{day}</span>
        ))}

        {calendarDays.map((day, index) => (
          <span
            key={index}
            className={`calendar-day ${isToday(day) ? 'today' : ''}`}
          >
            {day ?? ''}
          </span>
        ))}
      </div>

      <button
        className="today-button"
        onClick={() => setViewDate(
          new Date(today.getFullYear(), today.getMonth(), 1)
        )}
      >
        Today
      </button>
    </section>
  )
}

function Sidebar({ activePage, onPageChange }) {
  return (
    <aside className="sidebar">
      <div className="brand">medisphere<span>.</span></div>

      <div className="sidebar-label">WORKSPACE</div>

      <nav className="side-nav">
        {menuItems.map(({ name, icon: Icon }) => (
          <button
            key={name}
            onClick={() => onPageChange(name)}
            className={`nav-item ${activePage === name ? 'active' : ''}`}
          >
            <Icon size={19} strokeWidth={1.8} />
            <span>{name}</span>
          </button>
        ))}
      </nav>

      <div className="sidebar-bottom">
        <div className="sidebar-label">MEDISPHERE SYSTEM</div>
        <div className="sidebar-note">
          <Server size={17} />
          <span>Hospital Management</span>
        </div>
        <div className="sidebar-note">
          <Settings size={17} />
          <span>Demo Workspace</span>
        </div>
      </div>
    </aside>
  )
}

function Dashboard() {
  return (
    <>
      <div className="hero">
        <div className="eyebrow">MEDISPHERE / DASHBOARD</div>
        <h1>Hospital<br />Overview</h1>
        <p>Real-time insights. Smarter decisions. Better care.</p>
      </div>

      <div className="section-heading">
        <h2>Hospital at a glance</h2>
        <span className="demo-label">Sample data</span>
      </div>

      <div className="stats-grid">
        {statistics.map(({ label, value, color, icon: Icon }) => (
          <article key={label} className={`stat-card ${color}`}>
            <div className="stat-heading">
              <span>{label}</span>
              <Icon size={20} strokeWidth={1.7} />
            </div>
            <strong>{value}</strong>
            <small>Sample data</small>
          </article>
        ))}
      </div>

      <div className="dashboard-grid">
        <section className="panel">
          <div className="panel-title">
            <Wrench size={19} />
            <h3>Equipment Status</h3>
          </div>

          <div className="panel-list">
            {equipment.map(item => (
              <div className="list-row" key={item.name}>
                <span>{item.name}</span>
                <span className={
                  `status-pill ${item.status === 'Maintenance' ? 'warning' : 'success'}`
                }>
                  {item.status}
                </span>
              </div>
            ))}
          </div>
          <p className="panel-footnote">Illustrative equipment statuses</p>
        </section>

        <section className="panel">
          <div className="panel-title">
            <Clock3 size={19} />
            <h3>Upcoming Operations</h3>
          </div>

          <div className="panel-list">
            {operations.map((item, index) => (
              <div className="list-row operation-row" key={index}>
                <span className="operation-time">{item.time}</span>
                <span className="operation-name">{item.name}</span>
                <span className={`room-tag ${item.color}`}>{item.room}</span>
              </div>
            ))}
          </div>
          <p className="panel-footnote">Illustrative reservations</p>
        </section>

        <section className="panel">
          <div className="panel-title">
            <Activity size={19} />
            <h3>System Health</h3>
          </div>

          <div className="panel-list">
            {['Scheduling', 'Emergency', 'Donor'].map(service => (
              <div className="list-row health-row" key={service}>
                <span>{service}</span>
                <span className="status-pill unchecked">Not checked</span>
              </div>
            ))}
          </div>
          <p className="panel-footnote">
            Live status will appear after API integration.
          </p>
        </section>

        <HospitalCalendar />
      </div>
    </>
  )
}

function App() {
  const [activePage, setActivePage] = useState('Dashboard')
  const [search, setSearch] = useState('')

  return (
    <div className="app-shell">
      <Sidebar activePage={activePage} onPageChange={setActivePage} />

      <main className="main-content">
        <header className="topbar">
          <div className="topbar-caption">HOSPITAL MANAGEMENT SYSTEM</div>

          <div className="topbar-actions">
            <div className="search-field">
              <Search size={19} />
              <input
                value={search}
                onChange={event => setSearch(event.target.value)}
                placeholder="Search patients, rooms, or ID"
                aria-label="Search"
              />
            </div>

            <div className="profile">
              <span className="avatar">MS</span>
              <span>Demo Workspace</span>
              <ChevronDown size={16} />
            </div>
          </div>
        </header>

       {activePage === 'Dashboard' ? (
  <Dashboard />
) : activePage === 'OR Scheduling' ? (
  <Scheduling />
) : activePage === 'Patients' ? (
  <Patients />
  ) : activePage === 'Emergency Queue' ? (
  <EmergencyQueue />
  ) : activePage === 'Beds & Equipment' ? (
  <BedsEquipment />
  ) : activePage === 'Blood & Organ Donors' ? (
  <Donors />
) : (
  <section className="placeholder-page">
            <div className="eyebrow">MEDISPHERE / {activePage.toUpperCase()}</div>
            <h1>{activePage}</h1>
            <p>
              This page will be implemented next using your approved design
              and the corresponding Java backend service.
            </p>
            <button className="primary-button" onClick={() => setActivePage('Dashboard')}>
              Back to Dashboard
            </button>
          </section>
        )}
      </main>
    </div>
  )
}

export default App
