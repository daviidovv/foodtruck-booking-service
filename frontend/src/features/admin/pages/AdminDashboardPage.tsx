import { useState, useEffect } from 'react'
import { useNavigate } from 'react-router-dom'
import { useQuery } from '@tanstack/react-query'
import {
  Shield,
  LogOut,
  RefreshCw,
  MapPin,
  CalendarDays,
  Drumstick,
  Users,
  TrendingUp,
  Loader2,
  CheckCircle2,
  Clock,
  XCircle,
} from 'lucide-react'
import { api } from '@/lib/api'
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card'
import { Button } from '@/components/ui/button'
import { Badge } from '@/components/ui/badge'
import type { Location, Reservation } from '@/types'

export function AdminDashboardPage() {
  const navigate = useNavigate()
  const today = new Date().toISOString().split('T')[0]

  const [credentials, setCredentials] = useState<{ username: string; password: string } | null>(null)

  useEffect(() => {
    const storedCreds = sessionStorage.getItem('admin_credentials')

    if (!storedCreds) {
      navigate('/admin/login')
      return
    }

    setCredentials(JSON.parse(storedCreds))
  }, [navigate])

  const { data: locations, isLoading: locationsLoading } = useQuery({
    queryKey: ['locations'],
    queryFn: () => api.getLocations(),
  })

  const { data: allReservations, isLoading: reservationsLoading, refetch } = useQuery({
    queryKey: ['admin-reservations', today],
    queryFn: () => api.getAdminReservations(today, credentials!.username, credentials!.password),
    enabled: !!credentials,
    refetchInterval: 30000,
  })

  const handleLogout = () => {
    sessionStorage.removeItem('admin_credentials')
    navigate('/admin/login')
  }

  if (!credentials) {
    return (
      <div className="flex items-center justify-center min-h-screen">
        <Loader2 className="h-8 w-8 animate-spin" />
      </div>
    )
  }

  // Calculate statistics
  const stats = {
    totalReservations: allReservations?.length || 0,
    confirmedReservations: allReservations?.filter(r => r.status === 'CONFIRMED').length || 0,
    completedReservations: allReservations?.filter(r => r.status === 'COMPLETED').length || 0,
    totalChickens: allReservations?.reduce((sum, r) => sum + r.chickenCount, 0) || 0,
    totalFries: allReservations?.reduce((sum, r) => sum + r.friesCount, 0) || 0,
  }

  const getStatusBadge = (status: string) => {
    switch (status) {
      case 'CONFIRMED':
        return <Badge variant="default"><Clock className="h-3 w-3 mr-1" />Offen</Badge>
      case 'COMPLETED':
        return <Badge variant="success"><CheckCircle2 className="h-3 w-3 mr-1" />Abgeholt</Badge>
      case 'NO_SHOW':
        return <Badge variant="danger"><XCircle className="h-3 w-3 mr-1" />Nicht erschienen</Badge>
      case 'CANCELLED':
        return <Badge variant="muted">Storniert</Badge>
      default:
        return <Badge>{status}</Badge>
    }
  }

  return (
    <div className="min-h-screen bg-muted/30">
      {/* Header */}
      <header className="sticky top-0 z-50 border-b bg-background">
        <div className="container flex h-16 items-center justify-between">
          <div className="flex items-center gap-3">
            <div className="flex h-10 w-10 items-center justify-center rounded-full bg-orange-600">
              <Shield className="h-5 w-5 text-white" />
            </div>
            <div>
              <p className="font-semibold">Admin Dashboard</p>
              <p className="text-xs text-muted-foreground">Angemeldet als: {credentials.username}</p>
            </div>
          </div>
          <Button variant="ghost" size="sm" onClick={handleLogout}>
            <LogOut className="mr-2 h-4 w-4" />
            Abmelden
          </Button>
        </div>
      </header>

      <main className="container py-6 space-y-6">
        {/* Statistics Cards */}
        <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-4">
          <Card>
            <CardHeader className="flex flex-row items-center justify-between pb-2">
              <CardTitle className="text-sm font-medium">Reservierungen heute</CardTitle>
              <CalendarDays className="h-4 w-4 text-muted-foreground" />
            </CardHeader>
            <CardContent>
              <div className="text-2xl font-bold">{stats.totalReservations}</div>
              <p className="text-xs text-muted-foreground">
                {stats.confirmedReservations} offen, {stats.completedReservations} abgeholt
              </p>
            </CardContent>
          </Card>

          <Card>
            <CardHeader className="flex flex-row items-center justify-between pb-2">
              <CardTitle className="text-sm font-medium">Hähnchen verkauft</CardTitle>
              <Drumstick className="h-4 w-4 text-muted-foreground" />
            </CardHeader>
            <CardContent>
              <div className="text-2xl font-bold">{stats.totalChickens}</div>
              <p className="text-xs text-muted-foreground">
                + {stats.totalFries} Portionen Pommes
              </p>
            </CardContent>
          </Card>

          <Card>
            <CardHeader className="flex flex-row items-center justify-between pb-2">
              <CardTitle className="text-sm font-medium">Aktive Standorte</CardTitle>
              <MapPin className="h-4 w-4 text-muted-foreground" />
            </CardHeader>
            <CardContent>
              <div className="text-2xl font-bold">{locations?.content.length || 0}</div>
              <p className="text-xs text-muted-foreground">
                Foodtruck-Standorte
              </p>
            </CardContent>
          </Card>

          <Card>
            <CardHeader className="flex flex-row items-center justify-between pb-2">
              <CardTitle className="text-sm font-medium">Abholquote</CardTitle>
              <TrendingUp className="h-4 w-4 text-muted-foreground" />
            </CardHeader>
            <CardContent>
              <div className="text-2xl font-bold">
                {stats.totalReservations > 0
                  ? Math.round((stats.completedReservations / stats.totalReservations) * 100)
                  : 0}%
              </div>
              <p className="text-xs text-muted-foreground">
                Abgeholte Reservierungen
              </p>
            </CardContent>
          </Card>
        </div>

        {/* Locations Overview */}
        <Card>
          <CardHeader>
            <div className="flex items-center justify-between">
              <div>
                <CardTitle className="flex items-center gap-2">
                  <MapPin className="h-5 w-5" />
                  Standorte
                </CardTitle>
                <CardDescription>Alle aktiven Foodtruck-Standorte</CardDescription>
              </div>
            </div>
          </CardHeader>
          <CardContent>
            {locationsLoading ? (
              <div className="flex justify-center py-8">
                <Loader2 className="h-6 w-6 animate-spin" />
              </div>
            ) : (
              <div className="grid gap-4 md:grid-cols-2">
                {locations?.content.map((location: Location) => (
                  <div key={location.id} className="flex items-center justify-between p-4 border rounded-lg">
                    <div>
                      <p className="font-medium">{location.name}</p>
                      <p className="text-sm text-muted-foreground">{location.address}</p>
                    </div>
                    <Badge variant={location.active ? 'success' : 'muted'}>
                      {location.active ? 'Aktiv' : 'Inaktiv'}
                    </Badge>
                  </div>
                ))}
              </div>
            )}
          </CardContent>
        </Card>

        {/* Today's Reservations */}
        <Card>
          <CardHeader>
            <div className="flex items-center justify-between">
              <div>
                <CardTitle className="flex items-center gap-2">
                  <Users className="h-5 w-5" />
                  Heutige Reservierungen
                </CardTitle>
                <CardDescription>Alle Reservierungen für {new Date().toLocaleDateString('de-DE')}</CardDescription>
              </div>
              <Button variant="ghost" size="icon" onClick={() => refetch()}>
                <RefreshCw className="h-4 w-4" />
              </Button>
            </div>
          </CardHeader>
          <CardContent>
            {reservationsLoading ? (
              <div className="flex justify-center py-8">
                <Loader2 className="h-6 w-6 animate-spin" />
              </div>
            ) : allReservations?.length === 0 ? (
              <p className="text-center text-muted-foreground py-8">
                Keine Reservierungen für heute
              </p>
            ) : (
              <div className="space-y-3">
                {allReservations?.map((reservation: Reservation) => (
                  <div key={reservation.id} className="flex items-center justify-between p-4 border rounded-lg">
                    <div className="space-y-1">
                      <div className="flex items-center gap-2">
                        <span className="font-medium">{reservation.customerName}</span>
                        <span className="font-mono text-xs text-muted-foreground">
                          {reservation.confirmationCode}
                        </span>
                      </div>
                      <div className="flex items-center gap-4 text-sm text-muted-foreground">
                        <span>🍗 {reservation.chickenCount}x</span>
                        {reservation.friesCount > 0 && <span>🍟 {reservation.friesCount}x</span>}
                        {reservation.pickupTime && (
                          <span className="flex items-center gap-1">
                            <Clock className="h-3 w-3" />
                            {reservation.pickupTime.slice(0, 5)}
                          </span>
                        )}
                        <span className="text-xs">
                          <MapPin className="h-3 w-3 inline mr-1" />
                          {reservation.locationId.slice(0, 8)}...
                        </span>
                      </div>
                    </div>
                    {getStatusBadge(reservation.status)}
                  </div>
                ))}
              </div>
            )}
          </CardContent>
        </Card>

        {/* Quick Links */}
        <Card>
          <CardHeader>
            <CardTitle>Schnellzugriff</CardTitle>
          </CardHeader>
          <CardContent>
            <div className="flex gap-4">
              <Button variant="outline" onClick={() => navigate('/staff/login')}>
                <Users className="mr-2 h-4 w-4" />
                Mitarbeiter-Ansicht
              </Button>
              <Button variant="outline" onClick={() => navigate('/')}>
                <Drumstick className="mr-2 h-4 w-4" />
                Kunden-Ansicht
              </Button>
            </div>
          </CardContent>
        </Card>
      </main>
    </div>
  )
}
