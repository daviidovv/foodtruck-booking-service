import { Link } from 'react-router-dom'
import { ArrowLeft } from 'lucide-react'
import { Button } from '@/components/ui/button'

export function DatenschutzPage() {
  return (
    <div className="max-w-2xl mx-auto">
      <Link to="/">
        <Button variant="ghost" size="sm" className="mb-4 -ml-2">
          <ArrowLeft className="h-4 w-4 mr-2" />
          Zurück
        </Button>
      </Link>

      <h1 className="text-3xl font-bold mb-6">Datenschutzerklärung</h1>

      <div className="prose prose-neutral max-w-none space-y-6">
        <section>
          <h2 className="text-xl font-semibold mb-2">1. Verantwortlicher</h2>
          <p className="text-muted-foreground">
            Verantwortlich für die Datenverarbeitung auf dieser Website ist:
          </p>
          <p className="text-muted-foreground mt-2">
            Hendl Heinrich GmbH<br />
            Siegfried Heinrich<br />
            Am Mitterweg 4<br />
            D-83209 Prien a. Chiemsee<br />
            Telefon: 0171 7105524<br />
            E-Mail: info@hendl-heinrich.de
          </p>
        </section>

        <section>
          <h2 className="text-xl font-semibold mb-2">2. Erhobene Daten</h2>
          <p className="text-muted-foreground">
            Bei einer Reservierung erheben wir folgende personenbezogene Daten:
          </p>
          <ul className="list-disc list-inside text-muted-foreground mt-2 space-y-1">
            <li>Name</li>
            <li>Telefonnummer</li>
            <li>Reservierungsdetails (Datum, Uhrzeit, Anzahl)</li>
          </ul>
        </section>

        <section>
          <h2 className="text-xl font-semibold mb-2">3. Zweck der Datenverarbeitung</h2>
          <p className="text-muted-foreground">
            Wir verarbeiten Ihre Daten ausschließlich für folgende Zwecke:
          </p>
          <ul className="list-disc list-inside text-muted-foreground mt-2 space-y-1">
            <li>Abwicklung Ihrer Reservierung</li>
            <li>Kontaktaufnahme bei Rückfragen oder Änderungen</li>
            <li>Benachrichtigung falls Ihre Bestellung zur Abholung bereit ist</li>
          </ul>
          <p className="text-muted-foreground mt-2">
            Rechtsgrundlage ist Art. 6 Abs. 1 lit. b DSGVO (Vertragserfüllung).
          </p>
        </section>

        <section>
          <h2 className="text-xl font-semibold mb-2">4. Speicherdauer</h2>
          <p className="text-muted-foreground">
            Ihre Reservierungsdaten werden nach Abholung Ihrer Bestellung bzw. nach Ablauf des
            Reservierungstermins für maximal 30 Tage gespeichert und anschließend automatisch
            gelöscht.
          </p>
        </section>

        <section>
          <h2 className="text-xl font-semibold mb-2">5. Ihre Rechte</h2>
          <p className="text-muted-foreground">
            Sie haben jederzeit das Recht auf:
          </p>
          <ul className="list-disc list-inside text-muted-foreground mt-2 space-y-1">
            <li><strong>Auskunft</strong> über Ihre gespeicherten Daten (Art. 15 DSGVO)</li>
            <li><strong>Berichtigung</strong> unrichtiger Daten (Art. 16 DSGVO)</li>
            <li><strong>Löschung</strong> Ihrer Daten (Art. 17 DSGVO)</li>
            <li><strong>Einschränkung</strong> der Verarbeitung (Art. 18 DSGVO)</li>
            <li><strong>Widerspruch</strong> gegen die Verarbeitung (Art. 21 DSGVO)</li>
            <li><strong>Datenübertragbarkeit</strong> (Art. 20 DSGVO)</li>
          </ul>
          <p className="text-muted-foreground mt-2">
            Sie können Ihre Reservierung jederzeit selbst stornieren. Dabei werden Ihre Daten
            gelöscht.
          </p>
        </section>

        <section>
          <h2 className="text-xl font-semibold mb-2">6. Beschwerderecht</h2>
          <p className="text-muted-foreground">
            Sie haben das Recht, sich bei einer Datenschutz-Aufsichtsbehörde über die Verarbeitung
            Ihrer personenbezogenen Daten zu beschweren.
          </p>
        </section>

        <section>
          <h2 className="text-xl font-semibold mb-2">7. Cookies</h2>
          <p className="text-muted-foreground">
            Diese Website verwendet keine Tracking-Cookies und keine Analyse-Tools. Es werden
            lediglich technisch notwendige Cookies verwendet, die für den Betrieb der Website
            erforderlich sind.
          </p>
        </section>

        <section>
          <h2 className="text-xl font-semibold mb-2">8. SSL-Verschlüsselung</h2>
          <p className="text-muted-foreground">
            Diese Seite nutzt aus Sicherheitsgründen eine SSL-Verschlüsselung. Eine verschlüsselte
            Verbindung erkennen Sie an dem Schloss-Symbol in der Adresszeile Ihres Browsers.
          </p>
        </section>

        <section>
          <h2 className="text-xl font-semibold mb-2">9. Kontakt für Datenschutzanfragen</h2>
          <p className="text-muted-foreground">
            Bei Fragen zum Datenschutz erreichen Sie uns unter:<br />
            E-Mail: info@hendl-heinrich.de<br />
            Telefon: 0171 7105524
          </p>
        </section>

        <p className="text-sm text-muted-foreground/70 mt-8">
          Stand: Februar 2026
        </p>
      </div>
    </div>
  )
}
