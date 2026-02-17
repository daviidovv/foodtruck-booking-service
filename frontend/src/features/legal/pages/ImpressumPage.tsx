import { Link } from 'react-router-dom'
import { ArrowLeft } from 'lucide-react'
import { Button } from '@/components/ui/button'

export function ImpressumPage() {
  return (
    <div className="max-w-2xl mx-auto">
      <Link to="/">
        <Button variant="ghost" size="sm" className="mb-4 -ml-2">
          <ArrowLeft className="h-4 w-4 mr-2" />
          Zurück
        </Button>
      </Link>

      <h1 className="text-3xl font-bold mb-6">Impressum</h1>

      <div className="prose prose-neutral max-w-none space-y-6">
        <section>
          <h2 className="text-xl font-semibold mb-2">Angaben gemäß § 5 TMG</h2>
          <p className="text-muted-foreground">
            Hendl Heinrich GmbH<br />
            Am Mitterweg 4<br />
            D-83209 Prien a. Chiemsee
          </p>
        </section>

        <section>
          <h2 className="text-xl font-semibold mb-2">Vertreten durch</h2>
          <p className="text-muted-foreground">
            Siegfried Heinrich (Geschäftsführer)
          </p>
        </section>

        <section>
          <h2 className="text-xl font-semibold mb-2">Kontakt</h2>
          <p className="text-muted-foreground">
            Telefon: 0171 7105524<br />
            E-Mail: info@hendl-heinrich.de
          </p>
        </section>

        <section>
          <h2 className="text-xl font-semibold mb-2">Streitschlichtung</h2>
          <p className="text-muted-foreground">
            Die Europäische Kommission stellt eine Plattform zur Online-Streitbeilegung (OS) bereit:{' '}
            <a
              href="https://ec.europa.eu/consumers/odr/"
              target="_blank"
              rel="noopener noreferrer"
              className="text-primary hover:underline"
            >
              https://ec.europa.eu/consumers/odr/
            </a>
          </p>
          <p className="text-muted-foreground mt-2">
            Wir sind nicht bereit oder verpflichtet, an Streitbeilegungsverfahren vor einer
            Verbraucherschlichtungsstelle teilzunehmen.
          </p>
        </section>

        <section>
          <h2 className="text-xl font-semibold mb-2">Haftung für Inhalte</h2>
          <p className="text-muted-foreground">
            Als Diensteanbieter sind wir gemäß § 7 Abs.1 TMG für eigene Inhalte auf diesen Seiten
            nach den allgemeinen Gesetzen verantwortlich. Nach §§ 8 bis 10 TMG sind wir als
            Diensteanbieter jedoch nicht verpflichtet, übermittelte oder gespeicherte fremde
            Informationen zu überwachen oder nach Umständen zu forschen, die auf eine rechtswidrige
            Tätigkeit hinweisen.
          </p>
        </section>
      </div>
    </div>
  )
}
