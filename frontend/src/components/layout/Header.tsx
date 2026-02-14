import { Link, useLocation } from 'react-router-dom'
import { Drumstick, Search, Menu } from 'lucide-react'
import { Button } from '@/components/ui/button'
import { useState } from 'react'
import { cn } from '@/lib/utils'

export function Header() {
  const location = useLocation()
  const [mobileMenuOpen, setMobileMenuOpen] = useState(false)

  const isActive = (path: string) => location.pathname === path

  return (
    <header className="sticky top-0 z-50 w-full border-b bg-background/95 backdrop-blur supports-[backdrop-filter]:bg-background/60">
      <div className="container flex h-16 items-center justify-between">
        {/* Logo */}
        <Link to="/" className="flex items-center gap-2">
          <div className="flex h-10 w-10 items-center justify-center rounded-full bg-primary">
            <Drumstick className="h-6 w-6 text-white" />
          </div>
          <span className="hidden font-bold text-xl sm:inline-block">
            Hähnchen-Truck
          </span>
        </Link>

        {/* Desktop Navigation */}
        <nav className="hidden md:flex items-center gap-6">
          <Link
            to="/"
            className={cn(
              "text-sm font-medium transition-colors hover:text-primary",
              isActive('/') ? "text-primary" : "text-muted-foreground"
            )}
          >
            Reservieren
          </Link>
          <Link
            to="/lookup"
            className={cn(
              "text-sm font-medium transition-colors hover:text-primary",
              isActive('/lookup') ? "text-primary" : "text-muted-foreground"
            )}
          >
            Reservierung suchen
          </Link>
        </nav>

        {/* Actions */}
        <div className="flex items-center gap-2">
          <Link to="/lookup">
            <Button variant="outline" size="sm" className="hidden sm:flex gap-2">
              <Search className="h-4 w-4" />
              Code eingeben
            </Button>
          </Link>
          <Link to="/staff/login">
            <Button variant="ghost" size="sm">
              Mitarbeiter
            </Button>
          </Link>

          {/* Mobile menu button */}
          <Button
            variant="ghost"
            size="icon"
            className="md:hidden"
            onClick={() => setMobileMenuOpen(!mobileMenuOpen)}
          >
            <Menu className="h-5 w-5" />
          </Button>
        </div>
      </div>

      {/* Mobile Navigation */}
      {mobileMenuOpen && (
        <div className="md:hidden border-t">
          <nav className="container py-4 flex flex-col gap-2">
            <Link
              to="/"
              onClick={() => setMobileMenuOpen(false)}
              className={cn(
                "px-4 py-2 rounded-md text-sm font-medium transition-colors",
                isActive('/') ? "bg-primary text-white" : "hover:bg-muted"
              )}
            >
              Reservieren
            </Link>
            <Link
              to="/lookup"
              onClick={() => setMobileMenuOpen(false)}
              className={cn(
                "px-4 py-2 rounded-md text-sm font-medium transition-colors",
                isActive('/lookup') ? "bg-primary text-white" : "hover:bg-muted"
              )}
            >
              Reservierung suchen
            </Link>
          </nav>
        </div>
      )}
    </header>
  )
}
