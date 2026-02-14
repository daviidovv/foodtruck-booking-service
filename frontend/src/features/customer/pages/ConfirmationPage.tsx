import { useParams, Link } from 'react-router-dom'
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { CheckCircle2, MapPin, Clock, Loader2, Copy, XCircle } from 'lucide-react'
import { api } from '@/lib/api'
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card'
import { Button } from '@/components/ui/button'
import { Badge } from '@/components/ui/badge'
import { useState } from 'react'
import { formatTime } from '@/lib/utils'
import { ApiError } from '@/types'

export function ConfirmationPage() {
  const { code } = useParams<{ code: string }>()
  const queryClient = useQueryClient()
  const [copied, setCopied] = useState(false)
  const [showCancelConfirm, setShowCancelConfirm] = useState(false)
  const [cancelError, setCancelError] = useState<string | null>(null)

  const { data: reservation, isLoading, error } = useQuery({
    queryKey: ['reservation', code],
    queryFn: () => api.getReservationByCode(code!),
    enabled: !!code,
  })

  const cancelReservation = useMutation({
    mutationFn: () => api.cancelReservation(code!),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['reservation', code] })
      setShowCancelConfirm(false)
    },
    onError: (err: ApiError) => {
      setCancelError(err.detail || 'Stornierung fehlgeschlagen.')
    },
  })

  const copyCode = () => {
    if (code) {
      navigator.clipboard.writeText(code)
      setCopied(true)
      setTimeout(() => setCopied(false), 2000)
    }
  }

  if (isLoading) {
    return (
      <div className="flex items-center justify-center py-12">
        <Loader2 className="h-8 w-8 animate-spin text-primary" />
      </div>
    )
  }

  if (error || !reservation) {
    return (
      <div className="max-w-md mx-auto text-center py-12 space-y-4">
        <XCircle className="h-16 w-16 text-destructive mx-auto" />
        <h1 className="text-2xl font-bold">Reservierung nicht gefunden</h1>
        <p className="text-muted-foreground">
          Der Code "{code}" konnte nicht gefunden werden.
        </p>
        <Link to="/lookup">
          <Button>Erneut suchen</Button>
        </Link>
      </div>
    )
  }

  const isCancelled = reservation.status === 'CANCELLED'
  const isCompleted = reservation.status === 'COMPLETED'

  return (
    <div className="max-w-md mx-auto space-y-6">
      {/* Success header */}
      {!isCancelled && !isCompleted && (
        <div className="text-center space-y-2">
          <CheckCircle2 className="h-16 w-16 text-green-500 mx-auto" />
          <h1 className="text-2xl font-bold">Reservierung bestätigt!</h1>
          <p className="text-muted-foreground">
            Deine Reservierung wurde erfolgreich erstellt.
          </p>
        </div>
      )}

      {isCancelled && (
        <div className="text-center space-y-2">
          <XCircle className="h-16 w-16 text-destructive mx-auto" />
          <h1 className="text-2xl font-bold">Reservierung storniert</h1>
          <p className="text-muted-foreground">
            Diese Reservierung wurde storniert.
          </p>
        </div>
      )}

      {/* Confirmation code */}
      <Card className="border-2 border-primary">
        <CardHeader className="text-center pb-2">
          <CardDescription>Dein Bestätigungscode</CardDescription>
          <CardTitle className="text-4xl font-mono tracking-wider">
            {reservation.confirmationCode}
          </CardTitle>
        </CardHeader>
        <CardContent className="text-center">
          <Button
            variant="outline"
            size="sm"
            onClick={copyCode}
            className="gap-2"
          >
            <Copy className="h-4 w-4" />
            {copied ? 'Kopiert!' : 'Code kopieren'}
          </Button>
          <p className="text-xs text-muted-foreground mt-2">
            Zeige diesen Code bei der Abholung
          </p>
        </CardContent>
      </Card>

      {/* Reservation details */}
      <Card>
        <CardHeader>
          <CardTitle className="text-lg">Reservierungsdetails</CardTitle>
        </CardHeader>
        <CardContent className="space-y-4">
          <div className="flex items-start gap-3">
            <MapPin className="h-5 w-5 text-primary mt-0.5" />
            <div>
              <p className="font-medium">{reservation.locationName}</p>
              <p className="text-sm text-muted-foreground">{reservation.locationAddress}</p>
            </div>
          </div>

          {reservation.pickupTime && (
            <div className="flex items-center gap-3">
              <Clock className="h-5 w-5 text-primary" />
              <div>
                <p className="font-medium">Abholzeit</p>
                <p className="text-sm text-muted-foreground">
                  {formatTime(reservation.pickupTime)} Uhr
                </p>
              </div>
            </div>
          )}

          <div className="border-t pt-4 space-y-2">
            <div className="flex justify-between">
              <span>🍗 Hähnchen</span>
              <span className="font-medium">{reservation.chickenCount}x</span>
            </div>
            {reservation.friesCount > 0 && (
              <div className="flex justify-between">
                <span>🍟 Pommes</span>
                <span className="font-medium">{reservation.friesCount}x</span>
              </div>
            )}
          </div>

          <div className="flex items-center justify-between border-t pt-4">
            <span className="text-muted-foreground">Status</span>
            <Badge
              variant={
                reservation.status === 'CONFIRMED' ? 'success' :
                reservation.status === 'CANCELLED' ? 'destructive' :
                reservation.status === 'COMPLETED' ? 'secondary' :
                'muted'
              }
            >
              {reservation.status === 'CONFIRMED' && 'Bestätigt'}
              {reservation.status === 'CANCELLED' && 'Storniert'}
              {reservation.status === 'COMPLETED' && 'Abgeholt'}
              {reservation.status === 'NO_SHOW' && 'Nicht erschienen'}
            </Badge>
          </div>

          {reservation.notes && (
            <div className="border-t pt-4">
              <p className="text-sm text-muted-foreground">Anmerkungen</p>
              <p className="text-sm">{reservation.notes}</p>
            </div>
          )}
        </CardContent>
      </Card>

      {/* Cancel button */}
      {reservation.canCancel && (
        <Card>
          <CardContent className="pt-6">
            {!showCancelConfirm ? (
              <Button
                variant="outline"
                className="w-full text-destructive hover:text-destructive"
                onClick={() => setShowCancelConfirm(true)}
              >
                Reservierung stornieren
              </Button>
            ) : (
              <div className="space-y-4">
                <p className="text-center text-sm">
                  Möchtest du diese Reservierung wirklich stornieren?
                </p>
                {cancelError && (
                  <p className="text-center text-sm text-destructive">{cancelError}</p>
                )}
                <div className="flex gap-2">
                  <Button
                    variant="outline"
                    className="flex-1"
                    onClick={() => setShowCancelConfirm(false)}
                    disabled={cancelReservation.isPending}
                  >
                    Abbrechen
                  </Button>
                  <Button
                    variant="destructive"
                    className="flex-1"
                    onClick={() => cancelReservation.mutate()}
                    disabled={cancelReservation.isPending}
                  >
                    {cancelReservation.isPending ? (
                      <Loader2 className="h-4 w-4 animate-spin" />
                    ) : (
                      'Ja, stornieren'
                    )}
                  </Button>
                </div>
              </div>
            )}
          </CardContent>
        </Card>
      )}

      {/* Back to home */}
      <div className="text-center">
        <Link to="/">
          <Button variant="link">Neue Reservierung erstellen</Button>
        </Link>
      </div>
    </div>
  )
}
