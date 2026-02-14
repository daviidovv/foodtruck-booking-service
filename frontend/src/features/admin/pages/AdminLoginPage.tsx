import { useState } from 'react'
import { useNavigate, Link } from 'react-router-dom'
import { Shield, Loader2, ArrowLeft } from 'lucide-react'
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import { api } from '@/lib/api'

export function AdminLoginPage() {
  const navigate = useNavigate()
  const [credentials, setCredentials] = useState({ username: '', password: '' })
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState<string | null>(null)

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    setError(null)
    setLoading(true)

    try {
      // Verify admin credentials by calling an admin-only endpoint
      await api.verifyAdmin(credentials.username, credentials.password)

      // Store credentials in session storage
      sessionStorage.setItem('admin_credentials', JSON.stringify(credentials))
      navigate('/admin/dashboard')
    } catch {
      setError('Ungültige Admin-Anmeldedaten oder fehlende Berechtigung.')
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
              <div className="flex h-16 w-16 items-center justify-center rounded-full bg-orange-600">
                <Shield className="h-8 w-8 text-white" />
              </div>
            </div>
            <CardTitle className="text-2xl">Admin-Login</CardTitle>
            <CardDescription>
              Verwaltung und Statistiken
            </CardDescription>
          </CardHeader>
          <CardContent>
            <form onSubmit={handleSubmit} className="space-y-4">
              <div className="space-y-2">
                <Label htmlFor="username">Benutzername</Label>
                <Input
                  id="username"
                  placeholder="Admin-Benutzername"
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

              <Button type="submit" className="w-full bg-orange-600 hover:bg-orange-700" size="lg" disabled={loading}>
                {loading ? (
                  <>
                    <Loader2 className="mr-2 h-4 w-4 animate-spin" />
                    Wird angemeldet...
                  </>
                ) : (
                  'Als Admin anmelden'
                )}
              </Button>
            </form>
          </CardContent>
        </Card>

        <p className="text-center text-xs text-muted-foreground">
          Nur für autorisierte Administratoren
        </p>
      </div>
    </div>
  )
}
