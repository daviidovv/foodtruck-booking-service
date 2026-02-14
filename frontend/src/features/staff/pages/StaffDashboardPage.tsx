import { useState, useEffect } from 'react'
import { useNavigate } from 'react-router-dom'
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import {
  Drumstick,
  LogOut,
  RefreshCw,
  Package,
  CheckCircle2,
  XCircle,
  Clock,
  Loader2,
} from 'lucide-react'
import { api } from '@/lib/api'
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Badge } from '@/components/ui/badge'
import { Reservation, ReservationStatus, ApiError } from '@/types'
import { formatTime, formatDateTime } from '@/lib/utils'

export function StaffDashboardPage() {
  const navigate = useNavigate()
  const queryClient = useQueryClient()
  const today = new Date().toISOString().split('T')[0]

  const [credentials, setCredentials] = useState<{ username: string; password: string } | null>(null)
  const [locationId, setLocationId] = useState<string | null>(null)
  const [newInventory, setNewInventory] = useState('')

  useEffect(() => {
    const storedCreds = sessionStorage.getItem('staff_credentials')
    const storedLocation = sessionStorage.getItem('staff_location')

    if (!storedCreds) {
      navigate('/staff/login')
      return
    }

    setCredentials(JSON.parse(storedCreds))
    setLocationId(storedLocation)
  }, [navigate])

  const { data: inventory, isLoading: inventoryLoading } = useQuery({
    queryKey: ['staff-inventory', locationId],
    queryFn: () =>
      api.getInventory(locationId!, credentials!.username, credentials!.password),
    enabled: !!locationId && !!credentials,
    refetchInterval: 30000,
  })

  const { data: reservations, isLoading: reservationsLoading } = useQuery({
    queryKey: ['staff-reservations', locationId, today],
    queryFn: () =>
      api.getStaffReservations(locationId!, today, credentials!.username, credentials!.password),
    enabled: !!locationId && !!credentials,
    refetchInterval: 15000,
  })

  const setInventoryMutation = useMutation({
    mutationFn: (total: number) =>
      api.setInventory(locationId!, total, credentials!.username, credentials!.password),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['staff-inventory'] })
      setNewInventory('')
    },
  })

  const updateStatusMutation = useMutation({
    mutationFn: ({ reservationId, status }: { reservationId: string; status: string }) =>
      api.updateReservationStatus(
        reservationId,
        status,
        credentials!.username,
        credentials!.password
      ),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['staff-reservations'] })
      queryClient.invalidateQueries({ queryKey: ['staff-inventory'] })
    },
  })

  const handleLogout = () => {
    sessionStorage.removeItem('staff_credentials')
    sessionStorage.removeItem('staff_location')
    navigate('/staff/login')
  }

  const handleSetInventory = (e: React.FormEvent) => {
    e.preventDefault()
    const total = parseInt(newInventory, 10)
    if (!isNaN(total) && total >= 0) {
      setInventoryMutation.mutate(total)
    }
  }

  const handleStatusChange = (reservationId: string, status: string) => {
    updateStatusMutation.mutate({ reservationId, status })
  }

  if (!credentials || !locationId) {
    return (
      <div className="flex items-center justify-center min-h-screen">
        <Loader2 className="h-8 w-8 animate-spin" />
      </div>
    )
  }

  const activeReservations = reservations?.filter(
    (r) => r.status === 'CONFIRMED'
  ) || []

  const completedReservations = reservations?.filter(
    (r) => r.status === 'COMPLETED' || r.status === 'NO_SHOW' || r.status === 'CANCELLED'
  ) || []

  return (
    <div className="min-h-screen bg-muted/30">
      {/* Header */}
      <header className="sticky top-0 z-50 border-b bg-background">
        <div className="container flex h-16 items-center justify-between">
          <div className="flex items-center gap-3">
            <div className="flex h-10 w-10 items-center justify-center rounded-full bg-primary">
              <Drumstick className="h-5 w-5 text-white" />
            </div>
            <div>
              <p className="font-semibold">{inventory?.locationName || 'Dashboard'}</p>
              <p className="text-xs text-muted-foreground">Mitarbeiter: {credentials.username}</p>
            </div>
          </div>
          <Button variant="ghost" size="sm" onClick={handleLogout}>
            <LogOut className="mr-2 h-4 w-4" />
            Abmelden
          </Button>
        </div>
      </header>

      <main className="container py-6 space-y-6">
        {/* Inventory Card */}
        <Card>
          <CardHeader>
            <div className="flex items-center justify-between">
              <div>
                <CardTitle className="flex items-center gap-2">
                  <Package className="h-5 w-5" />
                  Tagesvorrat
                </CardTitle>
                <CardDescription>Heute verfügbare Hähnchen</CardDescription>
              </div>
              <Button
                variant="ghost"
                size="icon"
                onClick={() => queryClient.invalidateQueries({ queryKey: ['staff-inventory'] })}
              >
                <RefreshCw className="h-4 w-4" />
              </Button>
            </div>
          </CardHeader>
          <CardContent className="space-y-4">
            {inventoryLoading ? (
              <div className="flex justify-center py-4">
                <Loader2 className="h-6 w-6 animate-spin" />
              </div>
            ) : inventory?.inventorySet ? (
              <>
                <div className="grid grid-cols-3 gap-4 text-center">
                  <div className="p-4 rounded-lg bg-muted">
                    <p className="text-3xl font-bold">{inventory.totalChickens}</p>
                    <p className="text-sm text-muted-foreground">Gesamt</p>
                  </div>
                  <div className="p-4 rounded-lg bg-orange-100 dark:bg-orange-900/30">
                    <p className="text-3xl font-bold text-orange-600">{inventory.reservedChickens}</p>
                    <p className="text-sm text-muted-foreground">Reserviert</p>
                  </div>
                  <div className="p-4 rounded-lg bg-green-100 dark:bg-green-900/30">
                    <p className="text-3xl font-bold text-green-600">{inventory.availableChickens}</p>
                    <p className="text-sm text-muted-foreground">Verfügbar</p>
                  </div>
                </div>
                <div className="space-y-2">
                  <div className="flex justify-between text-sm">
                    <span>Auslastung</span>
                    <span>{inventory.utilizationPercent?.toFixed(0)}%</span>
                  </div>
                  <div className="h-3 w-full bg-muted rounded-full overflow-hidden">
                    <div
                      className="h-full bg-primary transition-all"
                      style={{ width: `${inventory.utilizationPercent || 0}%` }}
                    />
                  </div>
                </div>
              </>
            ) : (
              <div className="text-center py-4">
                <p className="text-muted-foreground mb-4">
                  Noch kein Vorrat eingetragen
                </p>
              </div>
            )}

            {/* Update inventory form */}
            <form onSubmit={handleSetInventory} className="flex gap-2">
              <Input
                type="number"
                min="0"
                placeholder="Anzahl Hähnchen"
                value={newInventory}
                onChange={(e) => setNewInventory(e.target.value)}
              />
              <Button type="submit" disabled={setInventoryMutation.isPending}>
                {setInventoryMutation.isPending ? (
                  <Loader2 className="h-4 w-4 animate-spin" />
                ) : (
                  'Setzen'
                )}
              </Button>
            </form>
          </CardContent>
        </Card>

        {/* Active Reservations */}
        <Card>
          <CardHeader>
            <div className="flex items-center justify-between">
              <div>
                <CardTitle>Aktive Reservierungen</CardTitle>
                <CardDescription>
                  {activeReservations.length} offene Reservierungen
                </CardDescription>
              </div>
              <Badge variant="default">{activeReservations.length}</Badge>
            </div>
          </CardHeader>
          <CardContent>
            {reservationsLoading ? (
              <div className="flex justify-center py-8">
                <Loader2 className="h-6 w-6 animate-spin" />
              </div>
            ) : activeReservations.length === 0 ? (
              <p className="text-center text-muted-foreground py-8">
                Keine aktiven Reservierungen
              </p>
            ) : (
              <div className="space-y-3">
                {activeReservations.map((reservation) => (
                  <ReservationCard
                    key={reservation.id}
                    reservation={reservation}
                    onStatusChange={handleStatusChange}
                    isUpdating={updateStatusMutation.isPending}
                  />
                ))}
              </div>
            )}
          </CardContent>
        </Card>

        {/* Completed Reservations */}
        {completedReservations.length > 0 && (
          <Card>
            <CardHeader>
              <CardTitle className="text-muted-foreground">Abgeschlossen</CardTitle>
            </CardHeader>
            <CardContent>
              <div className="space-y-3 opacity-60">
                {completedReservations.map((reservation) => (
                  <ReservationCard
                    key={reservation.id}
                    reservation={reservation}
                    onStatusChange={handleStatusChange}
                    isUpdating={false}
                    disabled
                  />
                ))}
              </div>
            </CardContent>
          </Card>
        )}
      </main>
    </div>
  )
}

function ReservationCard({
  reservation,
  onStatusChange,
  isUpdating,
  disabled = false,
}: {
  reservation: Reservation
  onStatusChange: (id: string, status: string) => void
  isUpdating: boolean
  disabled?: boolean
}) {
  return (
    <div className="flex items-center justify-between p-4 border rounded-lg">
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
              {formatTime(reservation.pickupTime)}
            </span>
          )}
        </div>
        {reservation.notes && (
          <p className="text-xs text-muted-foreground italic">
            "{reservation.notes}"
          </p>
        )}
      </div>

      {!disabled && reservation.status === 'CONFIRMED' && (
        <div className="flex gap-2">
          <Button
            size="sm"
            variant="outline"
            className="text-destructive"
            onClick={() => onStatusChange(reservation.id, 'NO_SHOW')}
            disabled={isUpdating}
          >
            <XCircle className="h-4 w-4" />
          </Button>
          <Button
            size="sm"
            variant="success"
            onClick={() => onStatusChange(reservation.id, 'COMPLETED')}
            disabled={isUpdating}
          >
            <CheckCircle2 className="h-4 w-4 mr-1" />
            Abgeholt
          </Button>
        </div>
      )}

      {disabled && (
        <Badge
          variant={
            reservation.status === 'COMPLETED' ? 'success' :
            reservation.status === 'CANCELLED' ? 'muted' :
            'danger'
          }
        >
          {reservation.status === 'COMPLETED' && 'Abgeholt'}
          {reservation.status === 'CANCELLED' && 'Storniert'}
          {reservation.status === 'NO_SHOW' && 'Nicht erschienen'}
        </Badge>
      )}
    </div>
  )
}
