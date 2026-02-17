import { Link, useLocation } from 'react-router-dom'
import { Drumstick, Search, Menu, CalendarDays } from 'lucide-react'
import { Button } from '@/components/ui/button'
import { useState } from 'react'
import { cn } from '@/lib/utils'

export function Header() {
  const location = useLocation()
  const [mobileMenuOpen, setMobileMenuOpen] = useState(false)

  const isActive = (path: string) => location.pathname === path

  return (
    <header className="sticky top-0 z-50 w-full border-b bg-primary text-primary-foreground shadow-md">
      <div className="container flex h-16 items-center justify-between">
        {/* Logo */}
        <Link to="/" className="flex items-center gap-3">
          <div className="flex h-11 w-11 items-center justify-center rounded-full bg-white/20 backdrop-blur">
            <Drumstick className="h-7 w-7 text-white" />
          </div>
          <div className="hidden sm:block">
            <span className="font-bold text-xl tracking-tight">
              Hendl Heinrich
            </span>
            <span className="block text-xs text-white/80 -mt-1">
              Seit Generationen knusprig
            </span>
          </div>
        </Link>

        {/* Desktop Navigation */}
        <nav className="hidden md:flex items-center gap-6">
          <Link
            to="/"
            className={cn(
              "text-sm font-medium transition-colors hover:text-white/80",
              isActive('/') ? "text-white border-b-2 border-white pb-1" : "text-white/70"
            )}
          >
            Reservieren
          </Link>
          <Link
            to="/wochenplan"
            className={cn(
              "text-sm font-medium transition-colors hover:text-white/80",
              isActive('/wochenplan') ? "text-white border-b-2 border-white pb-1" : "text-white/70"
            )}
          >
            Wochenplan
          </Link>
          <Link
            to="/lookup"
            className={cn(
              "text-sm font-medium transition-colors hover:text-white/80",
              isActive('/lookup') ? "text-white border-b-2 border-white pb-1" : "text-white/70"
            )}
          >
            Reservierung suchen
          </Link>
        </nav>

        {/* Actions */}
        <div className="flex items-center gap-2">
          <Link to="/lookup">
            <Button variant="outline" size="sm" className="hidden sm:flex gap-2 bg-white/10 border-white/30 text-white hover:bg-white/20">
              <Search className="h-4 w-4" />
              Code eingeben
            </Button>
          </Link>
          <Link to="/staff/login">
            <Button variant="ghost" size="sm" className="text-white/70 hover:text-white hover:bg-white/10">
              Mitarbeiter
            </Button>
          </Link>

          {/* Mobile menu button */}
          <Button
            variant="ghost"
            size="icon"
            className="md:hidden text-white hover:bg-white/10"
            onClick={() => setMobileMenuOpen(!mobileMenuOpen)}
          >
            <Menu className="h-5 w-5" />
          </Button>
        </div>
      </div>

      {/* Mobile Navigation */}
      {mobileMenuOpen && (
        <div className="md:hidden border-t border-white/20 bg-primary">
          <nav className="container py-4 flex flex-col gap-2">
            <Link
              to="/"
              onClick={() => setMobileMenuOpen(false)}
              className={cn(
                "px-4 py-2 rounded-md text-sm font-medium transition-colors",
                isActive('/') ? "bg-white/20 text-white" : "text-white/80 hover:bg-white/10"
              )}
            >
              Reservieren
            </Link>
            <Link
              to="/wochenplan"
              onClick={() => setMobileMenuOpen(false)}
              className={cn(
                "px-4 py-2 rounded-md text-sm font-medium transition-colors flex items-center gap-2",
                isActive('/wochenplan') ? "bg-white/20 text-white" : "text-white/80 hover:bg-white/10"
              )}
            >
              <CalendarDays className="h-4 w-4" />
              Wochenplan
            </Link>
            <Link
              to="/lookup"
              onClick={() => setMobileMenuOpen(false)}
              className={cn(
                "px-4 py-2 rounded-md text-sm font-medium transition-colors",
                isActive('/lookup') ? "bg-white/20 text-white" : "text-white/80 hover:bg-white/10"
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
