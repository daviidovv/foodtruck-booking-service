import { Link } from 'react-router-dom'

export function Footer() {
  return (
    <footer className="border-t bg-muted/30 mt-auto">
      <div className="container py-6">
        <div className="flex flex-col sm:flex-row items-center justify-between gap-4">
          <p className="text-sm text-muted-foreground">
            © {new Date().getFullYear()} Hendl Heinrich GmbH
          </p>
          <nav className="flex items-center gap-6">
            <Link
              to="/impressum"
              className="text-sm text-muted-foreground hover:text-foreground transition-colors"
            >
              Impressum
            </Link>
            <Link
              to="/datenschutz"
              className="text-sm text-muted-foreground hover:text-foreground transition-colors"
            >
              Datenschutz
            </Link>
          </nav>
        </div>
      </div>
    </footer>
  )
}
