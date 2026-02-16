import { useQuery } from '@tanstack/react-query'
import { Link } from 'react-router-dom'
import { MapPin, Clock, ChevronRight, CalendarDays, Flame } from 'lucide-react'
import { api } from '@/lib/api'
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card'
import { Button } from '@/components/ui/button'
import { Badge } from '@/components/ui/badge'
import { AvailabilityStatus } from '@/types'
import { formatDate, cn } from '@/lib/utils'

function getStatusBadge(status: AvailabilityStatus) {
  switch (status) {
    case 'AVAILABLE':
      return <Badge variant="success">Verfügbar</Badge>
    case 'LIMITED':
      return <Badge variant="warning">Begrenzt</Badge>
    case 'ALMOST_FULL':
      return <Badge variant="danger">Fast ausverkauft</Badge>
    case 'SOLD_OUT':
      return <Badge variant="danger">Ausverkauft</Badge>
    case 'NOT_AVAILABLE':
      return <Badge variant="muted">Nicht verfügbar</Badge>
    case 'CLOSED':
      return <Badge variant="muted">Geschlossen</Badge>
    default:
      return null
  }
}

export function HomePage() {
  const today = new Date().toISOString().split('T')[0]

  const { data: locationsData, isLoading } = useQuery({
    queryKey: ['locations', 'today'],
    queryFn: () => api.getTodayLocations(),
    staleTime: 60 * 1000, // 1 minute
  })

  const locations = locationsData?.content ?? []
  const noLocationsToday = !isLoading && locations.length === 0

  return (
    <div className="space-y-8">
      {/* Hero Section */}
      <section className="text-center space-y-4 py-8">
        <h1 className="text-4xl font-bold tracking-tight">
          Knusprige Hendl von
          <span className="text-primary"> Hendl Heinrich</span>
        </h1>
        <p className="text-xl text-muted-foreground max-w-2xl mx-auto">
          Seit Generationen servieren wir die besten Hähnchen der Region.
          Reserviere jetzt und hol dir dein Hendl ohne Wartezeit!
        </p>
        <p className="text-sm text-muted-foreground">
          {formatDate(today)}
        </p>
      </section>

      {/* Locations Grid */}
      <section className="space-y-4">
        <h2 className="text-2xl font-semibold">Unsere Standorte heute</h2>

        {isLoading ? (
          <div className="grid gap-4 md:grid-cols-2">
            {[1, 2].map((i) => (
              <Card key={i} className="animate-pulse">
                <CardHeader>
                  <div className="h-6 w-32 bg-muted rounded" />
                  <div className="h-4 w-48 bg-muted rounded" />
                </CardHeader>
                <CardContent>
                  <div className="h-10 w-full bg-muted rounded" />
                </CardContent>
              </Card>
            ))}
          </div>
        ) : noLocationsToday ? (
          <Card className="border-dashed">
            <CardContent className="py-12 text-center space-y-4">
              <CalendarDays className="h-12 w-12 mx-auto text-muted-foreground" />
              <div>
                <h3 className="text-lg font-semibold">Heute sind wir nicht unterwegs</h3>
                <p className="text-muted-foreground mt-1">
                  Schau dir an, wann und wo du uns findest!
                </p>
              </div>
              <Link to="/wochenplan">
                <Button variant="default" size="lg">
                  <CalendarDays className="mr-2 h-4 w-4" />
                  Wochenplan ansehen
                </Button>
              </Link>
            </CardContent>
          </Card>
        ) : (
          <div className="grid gap-4 md:grid-cols-2">
            {locations.map((location) => (
              <LocationCard key={location.id} locationId={location.id} />
            ))}
          </div>
        )}
      </section>

      {/* Info Section */}
      <section className="bg-secondary/50 rounded-lg p-6 space-y-4">
        <h3 className="font-semibold text-lg">So funktioniert's</h3>
        <div className="grid gap-4 sm:grid-cols-3">
          <div className="flex gap-3">
            <div className="flex h-8 w-8 shrink-0 items-center justify-center rounded-full bg-primary text-white font-bold">
              1
            </div>
            <div>
              <p className="font-medium">Standort wählen</p>
              <p className="text-sm text-muted-foreground">
                Wähle einen unserer Standorte
              </p>
            </div>
          </div>
          <div className="flex gap-3">
            <div className="flex h-8 w-8 shrink-0 items-center justify-center rounded-full bg-primary text-white font-bold">
              2
            </div>
            <div>
              <p className="font-medium">Reservieren</p>
              <p className="text-sm text-muted-foreground">
                Gib deine Daten ein und wähle die Menge
              </p>
            </div>
          </div>
          <div className="flex gap-3">
            <div className="flex h-8 w-8 shrink-0 items-center justify-center rounded-full bg-primary text-white font-bold">
              3
            </div>
            <div>
              <p className="font-medium">Abholen</p>
              <p className="text-sm text-muted-foreground">
                Komm vorbei und zeig deinen Code
              </p>
            </div>
          </div>
        </div>
      </section>
    </div>
  )
}

function AvailabilityDisplay({ available, total }: { available: number; total: number }) {
  const percentage = (available / total) * 100
  const isLow = percentage <= 30
  const isMedium = percentage > 30 && percentage <= 60

  // Determine urgency level and styling
  const getUrgencyStyle = () => {
    if (available === 0) {
      return {
        bg: 'bg-destructive/10',
        text: 'text-destructive',
        border: 'border-destructive/30',
        icon: false,
        message: 'Ausverkauft'
      }
    }
    if (isLow) {
      return {
        bg: 'bg-destructive/10',
        text: 'text-destructive',
        border: 'border-destructive/30',
        icon: true,
        message: `Nur noch ${available}!`
      }
    }
    if (isMedium) {
      return {
        bg: 'bg-warning/10',
        text: 'text-warning',
        border: 'border-warning/30',
        icon: false,
        message: `Noch ${available} verfügbar`
      }
    }
    return {
      bg: 'bg-success/10',
      text: 'text-success',
      border: 'border-success/30',
      icon: false,
      message: `${available} Hähnchen verfügbar`
    }
  }

  const style = getUrgencyStyle()

  return (
    <div className={cn(
      "rounded-lg p-3 border transition-all",
      style.bg,
      style.border
    )}>
      <div className="flex items-center justify-between">
        <div className="flex items-center gap-2">
          {style.icon && (
            <Flame className={cn("h-4 w-4 animate-pulse", style.text)} />
          )}
          <span className={cn("font-semibold", style.text)}>
            {style.message}
          </span>
        </div>
        {!isLow && available > 0 && (
          <span className="text-xs text-muted-foreground">
            von {total}
          </span>
        )}
      </div>
      {isLow && available > 0 && (
        <p className="text-xs text-muted-foreground mt-1">
          Jetzt reservieren, bevor sie weg sind!
        </p>
      )}
    </div>
  )
}

function LocationCard({ locationId }: { locationId: string }) {
  const { data: availability, isLoading } = useQuery({
    queryKey: ['availability', locationId],
    queryFn: () => api.getAvailability(locationId),
    refetchInterval: 30000, // Refresh every 30 seconds
  })

  if (isLoading || !availability) {
    return (
      <Card className="animate-pulse">
        <CardHeader>
          <div className="h-6 w-32 bg-muted rounded" />
        </CardHeader>
      </Card>
    )
  }

  const canReserve =
    availability.isOpen &&
    availability.inventorySet &&
    availability.availableChickens > 0

  return (
    <Card className="overflow-hidden">
      <CardHeader>
        <div className="flex items-start justify-between">
          <div>
            <CardTitle className="flex items-center gap-2">
              <MapPin className="h-5 w-5 text-primary" />
              {availability.locationName}
            </CardTitle>
            <CardDescription className="mt-1">
              {availability.dayName}
            </CardDescription>
          </div>
          {getStatusBadge(availability.availabilityStatus)}
        </div>
      </CardHeader>
      <CardContent className="space-y-4">
        {availability.isOpen && availability.openingTime && (
          <div className="flex items-center gap-2 text-sm text-muted-foreground">
            <Clock className="h-4 w-4" />
            {availability.openingTime.substring(0, 5)} - {availability.closingTime?.substring(0, 5)} Uhr
          </div>
        )}

        {availability.inventorySet && availability.availableChickens !== null && (
          <AvailabilityDisplay
            available={availability.availableChickens}
            total={availability.totalChickens || 1}
          />
        )}

        {!availability.isOpen && (
          <p className="text-sm text-muted-foreground">
            Dieser Standort ist heute geschlossen.
          </p>
        )}

        {availability.isOpen && !availability.inventorySet && (
          <p className="text-sm text-muted-foreground">
            {availability.message || 'Vorrat wird noch eingetragen...'}
          </p>
        )}

        <Link to={`/reserve/${locationId}`}>
          <Button
            className="w-full"
            size="lg"
            disabled={!canReserve}
          >
            {canReserve ? (
              <>
                Jetzt reservieren
                <ChevronRight className="ml-2 h-4 w-4" />
              </>
            ) : availability.availabilityStatus === 'SOLD_OUT' ? (
              'Ausverkauft'
            ) : (
              'Nicht verfügbar'
            )}
          </Button>
        </Link>
      </CardContent>
    </Card>
  )
}
