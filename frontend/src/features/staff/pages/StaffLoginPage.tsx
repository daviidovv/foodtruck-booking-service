import { useState } from 'react'
import { useNavigate, Link } from 'react-router-dom'
import { Drumstick, Loader2, ArrowLeft } from 'lucide-react'
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import { api } from '@/lib/api'

// Mapping: Welcher Wagen fährt an welchem Tag wohin (ISO weekday: 1=Mo, 7=So)
const TRUCK_SCHEDULE: Record<string, Record<number, string>> = {
  wagen1: {
    2: 'Traunreut',     // Dienstag
    3: 'Mitterfelden',  // Mittwoch
    4: 'Siegsdorf',     // Donnerstag
    5: 'Traunreut',     // Freitag
  },
  wagen2: {
    2: 'Raubling',      // Dienstag
    3: 'Bad Endorf',    // Mittwoch
    4: 'Bruckmühl',     // Donnerstag
    5: 'Prien',         // Freitag
  },
}

function getIsoWeekday(): number {
  const day = new Date().getDay()
  return day === 0 ? 7 : day // Convert Sunday from 0 to 7
}

export function StaffLoginPage() {
  const navigate = useNavigate()
  const [credentials, setCredentials] = useState({ username: '', password: '' })
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState<string | null>(null)

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    setError(null)
    setLoading(true)

    try {
      // Hole alle Standorte
      const { content: locations } = await api.getLocations()

      // Prüfe ob Benutzer bekannt ist
      const truckSchedule = TRUCK_SCHEDULE[credentials.username]
      if (!truckSchedule) {
        setError('Unbekannter Benutzer')
        setLoading(false)
        return
      }

      // Ermittle heutigen Standort für diesen Wagen
      const isoDay = getIsoWeekday()
      const todayLocationName = truckSchedule[isoDay]

      let targetLocationId: string

      if (!todayLocationName) {
        // Ruhetag - trotzdem einloggen erlauben, aber ersten passenden Standort wählen
        // Wähle den ersten Standort aus dem Schedule dieses Wagens
        const firstScheduleDay = Object.keys(truckSchedule)[0]
        const firstLocationName = truckSchedule[Number(firstScheduleDay)]
        const firstLocation = locations.find(l => l.name === firstLocationName)

        if (!firstLocation) {
          setError('Kein Standort gefunden')
          setLoading(false)
          return
        }
        targetLocationId = firstLocation.id
      } else {
        // Normaler Tag - finde den heutigen Standort
        const todayLocation = locations.find(l => l.name === todayLocationName)
        if (!todayLocation) {
          setError(`Standort "${todayLocationName}" nicht gefunden`)
          setLoading(false)
          return
        }
        targetLocationId = todayLocation.id
      }

      // Validiere Credentials gegen Staff-Endpoint
      await api.getStaffReservations(
        targetLocationId,
        new Date().toISOString().split('T')[0],
        credentials.username,
        credentials.password
      )

      // Erfolg - speichere und weiterleiten
      sessionStorage.setItem('staff_credentials', JSON.stringify(credentials))
      sessionStorage.setItem('staff_location', targetLocationId)
      navigate('/staff/dashboard')

    } catch (err: unknown) {
      const apiError = err as { status?: number }
      if (apiError?.status === 401 || apiError?.status === 403) {
        setError('Ungültige Anmeldedaten')
      } else {
        setError('Verbindungsfehler. Bitte versuche es erneut.')
      }
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="min-h-screen bg-muted/30 flex items-center justify-center p-4">
      <div className="w-full max-w-md space-y-6">
        {/* Back to customer site */}
        <Link to="/" className="inline-flex items-center text-sm text-muted-foreground hover:text-primary">
          <ArrowLeft className="mr-2 h-4 w-4" />
          Zurück zur Kundenansicht
        </Link>

        {/* Login card */}
        <Card>
          <CardHeader className="text-center">
            <div className="flex justify-center mb-4">
              <div className="flex h-16 w-16 items-center justify-center rounded-full bg-primary">
                <Drumstick className="h-8 w-8 text-white" />
              </div>
            </div>
            <CardTitle className="text-2xl">Mitarbeiter-Login</CardTitle>
            <CardDescription>
              Melde dich an, um Reservierungen zu verwalten.
            </CardDescription>
          </CardHeader>
          <CardContent>
            <form onSubmit={handleSubmit} className="space-y-4">
              <div className="space-y-2">
                <Label htmlFor="username">Benutzername</Label>
                <Input
                  id="username"
                  placeholder="Benutzername"
                  value={credentials.username}
                  onChange={(e) => setCredentials({ ...credentials, username: e.target.value })}
                  required
                  autoFocus
                />
              </div>

              <div className="space-y-2">
                <Label htmlFor="password">Passwort</Label>
                <Input
                  id="password"
                  type="password"
                  placeholder="Passwort"
                  value={credentials.password}
                  onChange={(e) => setCredentials({ ...credentials, password: e.target.value })}
                  required
                />
              </div>

              {error && (
                <div className="p-3 bg-destructive/10 border border-destructive/20 rounded-lg text-destructive text-sm text-center">
                  {error}
                </div>
              )}

              <Button type="submit" className="w-full" size="lg" disabled={loading}>
                {loading ? (
                  <>
                    <Loader2 className="mr-2 h-4 w-4 animate-spin" />
                    Wird angemeldet...
                  </>
                ) : (
                  'Anmelden'
                )}
              </Button>
            </form>
          </CardContent>
        </Card>

        <p className="text-center text-xs text-muted-foreground">
          Hähnchen-Truck Reservierungssystem
        </p>
      </div>
    </div>
  )
}
