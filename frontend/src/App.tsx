import { lazy, Suspense } from 'react'
import { Routes, Route } from 'react-router-dom'
import { Loader2 } from 'lucide-react'
import { Header } from '@/components/layout/Header'
import { HomePage } from '@/features/customer/pages/HomePage'
import { LookupPage } from '@/features/customer/pages/LookupPage'
import { ReservationPage } from '@/features/customer/pages/ReservationPage'
import { ConfirmationPage } from '@/features/customer/pages/ConfirmationPage'
import { SchedulePage } from '@/features/customer/pages/SchedulePage'

// Lazy load staff and admin pages - these won't be in the customer bundle
const StaffLoginPage = lazy(() =>
  import('@/features/staff/pages/StaffLoginPage').then(m => ({ default: m.StaffLoginPage }))
)
const StaffDashboardPage = lazy(() =>
  import('@/features/staff/pages/StaffDashboardPage').then(m => ({ default: m.StaffDashboardPage }))
)
const AdminLoginPage = lazy(() =>
  import('@/features/admin/pages/AdminLoginPage').then(m => ({ default: m.AdminLoginPage }))
)
const AdminDashboardPage = lazy(() =>
  import('@/features/admin/pages/AdminDashboardPage').then(m => ({ default: m.AdminDashboardPage }))
)

// Loading fallback for lazy-loaded routes
function PageLoader() {
  return (
    <div className="flex items-center justify-center min-h-screen">
      <Loader2 className="h-8 w-8 animate-spin text-primary" />
    </div>
  )
}

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
                  <Route path="/wochenplan" element={<SchedulePage />} />
                  <Route path="/reserve/:locationId" element={<ReservationPage />} />
                  <Route path="/confirmation/:code" element={<ConfirmationPage />} />
                  <Route path="/lookup" element={<LookupPage />} />
                </Routes>
              </main>
            </>
          }
        />

        {/* Staff routes - lazy loaded, separate bundle */}
        <Route
          path="/staff/login"
          element={
            <Suspense fallback={<PageLoader />}>
              <StaffLoginPage />
            </Suspense>
          }
        />
        <Route
          path="/staff/dashboard"
          element={
            <Suspense fallback={<PageLoader />}>
              <StaffDashboardPage />
            </Suspense>
          }
        />

        {/* Admin routes - lazy loaded, separate bundle */}
        <Route
          path="/admin/login"
          element={
            <Suspense fallback={<PageLoader />}>
              <AdminLoginPage />
            </Suspense>
          }
        />
        <Route
          path="/admin/dashboard"
          element={
            <Suspense fallback={<PageLoader />}>
              <AdminDashboardPage />
            </Suspense>
          }
        />
      </Routes>
    </div>
  )
}

export default App
