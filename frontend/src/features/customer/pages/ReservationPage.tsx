import { useState } from 'react'
import { useParams, useNavigate, Link } from 'react-router-dom'
import { useQuery, useMutation } from '@tanstack/react-query'
import { ArrowLeft, Minus, Plus, Loader2 } from 'lucide-react'
import { api } from '@/lib/api'
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import { Badge } from '@/components/ui/badge'
import { CreateReservationRequest, ApiError } from '@/types'

export function ReservationPage() {
  const { locationId } = useParams<{ locationId: string }>()
  const navigate = useNavigate()

  const [formData, setFormData] = useState({
    customerName: '',
    customerEmail: '',
    chickenCount: 1,
    friesCount: 0,
    pickupTime: '',
    notes: '',
    privacyAccepted: false,
  })
  const [error, setError] = useState<string | null>(null)

  const { data: availability } = useQuery({
    queryKey: ['availability', locationId],
    queryFn: () => api.getAvailability(locationId!),
    enabled: !!locationId,
  })

  const createReservation = useMutation({
    mutationFn: (data: CreateReservationRequest) => api.createReservation(data),
    onSuccess: (reservation) => {
      navigate(`/confirmation/${reservation.confirmationCode}`)
    },
    onError: (err: ApiError) => {
      setError(err.detail || 'Ein Fehler ist aufgetreten.')
    },
  })

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault()
    setError(null)

    if (!locationId) return
    if (formData.chickenCount + formData.friesCount === 0) {
      setError('Bitte wähle mindestens ein Produkt aus.')
      return
    }
    if (!formData.privacyAccepted) {
      setError('Bitte stimme der Datenschutzerklärung zu.')
      return
    }

    createReservation.mutate({
      locationId,
      customerName: formData.customerName,
      customerEmail: formData.customerEmail || undefined,
      chickenCount: formData.chickenCount,
      friesCount: formData.friesCount,
      pickupTime: formData.pickupTime || undefined,
      notes: formData.notes || undefined,
    })
  }

  const updateCount = (field: 'chickenCount' | 'friesCount', delta: number) => {
    setFormData((prev) => ({
      ...prev,
      [field]: Math.max(0, Math.min(50, prev[field] + delta)),
    }))
  }

  if (!availability) {
    return (
      <div className="flex items-center justify-center py-12">
        <Loader2 className="h-8 w-8 animate-spin text-primary" />
      </div>
    )
  }

  const maxChickens = availability.availableChickens || 0

  return (
    <div className="max-w-2xl mx-auto space-y-6">
      {/* Back button */}
      <Link to="/" className="inline-flex items-center text-sm text-muted-foreground hover:text-primary">
        <ArrowLeft className="mr-2 h-4 w-4" />
        Zurück zur Standortauswahl
      </Link>

      {/* Location info */}
      <Card>
        <CardHeader>
          <div className="flex items-start justify-between">
            <div>
              <CardTitle>{availability.locationName}</CardTitle>
              <CardDescription>
                {availability.dayName} • {availability.openingTime?.substring(0, 5)} - {availability.closingTime?.substring(0, 5)} Uhr
              </CardDescription>
            </div>
            <Badge variant="success">
              {availability.availableChickens} verfügbar
            </Badge>
          </div>
        </CardHeader>
      </Card>

      {/* Reservation form */}
      <form onSubmit={handleSubmit}>
        <Card>
          <CardHeader>
            <CardTitle>Reservierung erstellen</CardTitle>
            <CardDescription>
              Fülle das Formular aus und sichere dir deine Hähnchen!
            </CardDescription>
          </CardHeader>
          <CardContent className="space-y-6">
            {/* Personal info */}
            <div className="space-y-4">
              <div className="space-y-2">
                <Label htmlFor="name">Name *</Label>
                <Input
                  id="name"
                  placeholder="Dein Name"
                  value={formData.customerName}
                  onChange={(e) => setFormData({ ...formData, customerName: e.target.value })}
                  required
                />
              </div>

              <div className="space-y-2">
                <Label htmlFor="email">E-Mail (optional)</Label>
                <Input
                  id="email"
                  type="email"
                  placeholder="deine@email.de"
                  value={formData.customerEmail}
                  onChange={(e) => setFormData({ ...formData, customerEmail: e.target.value })}
                />
                <p className="text-xs text-muted-foreground">
                  Für eine Bestätigungsmail (optional)
                </p>
              </div>
            </div>

            {/* Product selection */}
            <div className="space-y-4">
              <h3 className="font-medium">Produkte auswählen</h3>

              {/* Chicken counter */}
              <div className="flex items-center justify-between p-4 border rounded-lg">
                <div>
                  <p className="font-medium">🍗 Hähnchen</p>
                  <p className="text-sm text-muted-foreground">
                    Noch {maxChickens} verfügbar
                  </p>
                </div>
                <div className="flex items-center gap-3">
                  <Button
                    type="button"
                    variant="outline"
                    size="icon"
                    onClick={() => updateCount('chickenCount', -1)}
                    disabled={formData.chickenCount <= 0}
                  >
                    <Minus className="h-4 w-4" />
                  </Button>
                  <span className="w-8 text-center font-semibold text-lg">
                    {formData.chickenCount}
                  </span>
                  <Button
                    type="button"
                    variant="outline"
                    size="icon"
                    onClick={() => updateCount('chickenCount', 1)}
                    disabled={formData.chickenCount >= maxChickens}
                  >
                    <Plus className="h-4 w-4" />
                  </Button>
                </div>
              </div>

              {/* Fries counter */}
              <div className="flex items-center justify-between p-4 border rounded-lg">
                <div>
                  <p className="font-medium">🍟 Pommes</p>
                  <p className="text-sm text-muted-foreground">
                    Immer verfügbar
                  </p>
                </div>
                <div className="flex items-center gap-3">
                  <Button
                    type="button"
                    variant="outline"
                    size="icon"
                    onClick={() => updateCount('friesCount', -1)}
                    disabled={formData.friesCount <= 0}
                  >
                    <Minus className="h-4 w-4" />
                  </Button>
                  <span className="w-8 text-center font-semibold text-lg">
                    {formData.friesCount}
                  </span>
                  <Button
                    type="button"
                    variant="outline"
                    size="icon"
                    onClick={() => updateCount('friesCount', 1)}
                    disabled={formData.friesCount >= 50}
                  >
                    <Plus className="h-4 w-4" />
                  </Button>
                </div>
              </div>
            </div>

            {/* Pickup time */}
            <div className="space-y-2">
              <Label htmlFor="pickupTime">Gewünschte Abholzeit (optional)</Label>
              <Input
                id="pickupTime"
                type="time"
                value={formData.pickupTime}
                onChange={(e) => setFormData({ ...formData, pickupTime: e.target.value })}
                min={availability.openingTime?.substring(0, 5)}
                max={availability.closingTime?.substring(0, 5)}
              />
              <p className="text-xs text-muted-foreground">
                Du kannst auch ohne feste Abholzeit kommen
              </p>
            </div>

            {/* Notes */}
            <div className="space-y-2">
              <Label htmlFor="notes">Anmerkungen (optional)</Label>
              <Input
                id="notes"
                placeholder="z.B. extra knusprig"
                value={formData.notes}
                onChange={(e) => setFormData({ ...formData, notes: e.target.value })}
              />
            </div>

            {/* Privacy consent */}
            <div className="flex items-start gap-3 p-4 border rounded-lg bg-muted/30">
              <input
                type="checkbox"
                id="privacy"
                checked={formData.privacyAccepted}
                onChange={(e) => setFormData({ ...formData, privacyAccepted: e.target.checked })}
                className="mt-1 h-4 w-4 rounded border-gray-300 text-primary focus:ring-primary"
              />
              <label htmlFor="privacy" className="text-sm text-muted-foreground">
                Ich habe die{' '}
                <Link to="/datenschutz" className="text-primary hover:underline" target="_blank">
                  Datenschutzerklärung
                </Link>{' '}
                gelesen und stimme der Verarbeitung meiner Daten zur Reservierungsabwicklung zu. *
              </label>
            </div>

            {/* Error message */}
            {error && (
              <div className="p-3 bg-destructive/10 border border-destructive/20 rounded-lg text-destructive text-sm">
                {error}
              </div>
            )}

            {/* Submit button */}
            <Button
              type="submit"
              size="xl"
              className="w-full"
              disabled={createReservation.isPending || formData.chickenCount + formData.friesCount === 0}
            >
              {createReservation.isPending ? (
                <>
                  <Loader2 className="mr-2 h-4 w-4 animate-spin" />
                  Wird reserviert...
                </>
              ) : (
                'Jetzt reservieren'
              )}
            </Button>
          </CardContent>
        </Card>
      </form>
    </div>
  )
}
