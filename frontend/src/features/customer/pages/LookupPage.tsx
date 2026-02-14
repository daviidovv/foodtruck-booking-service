import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { Search, Loader2 } from 'lucide-react'
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import { api } from '@/lib/api'
import { ApiError } from '@/types'

export function LookupPage() {
  const navigate = useNavigate()
  const [code, setCode] = useState('')
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState<string | null>(null)

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    setError(null)

    const trimmedCode = code.trim().toUpperCase()
    if (!trimmedCode) {
      setError('Bitte gib einen Bestätigungscode ein.')
      return
    }

    setLoading(true)
    try {
      await api.getReservationByCode(trimmedCode)
      navigate(`/confirmation/${trimmedCode}`)
    } catch (err) {
      const apiError = err as ApiError
      if (apiError.status === 404) {
        setError('Reservierung nicht gefunden. Bitte überprüfe den Code.')
      } else {
        setError('Ein Fehler ist aufgetreten. Bitte versuche es erneut.')
      }
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="max-w-md mx-auto space-y-6 py-8">
      <div className="text-center space-y-2">
        <Search className="h-12 w-12 text-primary mx-auto" />
        <h1 className="text-2xl font-bold">Reservierung suchen</h1>
        <p className="text-muted-foreground">
          Gib deinen Bestätigungscode ein, um deine Reservierung anzuzeigen.
        </p>
      </div>

      <Card>
        <CardHeader>
          <CardTitle className="text-lg">Bestätigungscode</CardTitle>
          <CardDescription>
            Du hast den Code bei der Reservierung erhalten.
          </CardDescription>
        </CardHeader>
        <CardContent>
          <form onSubmit={handleSubmit} className="space-y-4">
            <div className="space-y-2">
              <Label htmlFor="code">Code</Label>
              <Input
                id="code"
                placeholder="z.B. 3R8GR2JR"
                value={code}
                onChange={(e) => setCode(e.target.value.toUpperCase())}
                className="text-center text-xl font-mono tracking-wider"
                maxLength={8}
                autoFocus
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
                  Wird gesucht...
                </>
              ) : (
                <>
                  <Search className="mr-2 h-4 w-4" />
                  Reservierung anzeigen
                </>
              )}
            </Button>
          </form>
        </CardContent>
      </Card>

      <p className="text-center text-sm text-muted-foreground">
        Du kannst hier auch deine Reservierung stornieren.
      </p>
    </div>
  )
}
