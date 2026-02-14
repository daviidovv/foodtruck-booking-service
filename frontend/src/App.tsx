import { Routes, Route } from 'react-router-dom'
import { Header } from '@/components/layout/Header'
import { HomePage } from '@/features/customer/pages/HomePage'
import { LookupPage } from '@/features/customer/pages/LookupPage'
import { ReservationPage } from '@/features/customer/pages/ReservationPage'
import { ConfirmationPage } from '@/features/customer/pages/ConfirmationPage'
import { StaffLoginPage } from '@/features/staff/pages/StaffLoginPage'
import { StaffDashboardPage } from '@/features/staff/pages/StaffDashboardPage'

function App() {
  return (
    <div className="min-h-screen bg-background">
      <Routes>
        {/* Customer routes with header */}
        <Route
          path="/*"
          element={
            <>
              <Header />
              <main className="container py-6">
                <Routes>
                  <Route path="/" element={<HomePage />} />
                  <Route path="/reserve/:locationId" element={<ReservationPage />} />
                  <Route path="/confirmation/:code" element={<ConfirmationPage />} />
                  <Route path="/lookup" element={<LookupPage />} />
                </Routes>
              </main>
            </>
          }
        />

        {/* Staff routes without customer header */}
        <Route path="/staff/login" element={<StaffLoginPage />} />
        <Route path="/staff/dashboard" element={<StaffDashboardPage />} />
      </Routes>
    </div>
  )
}

export default App
