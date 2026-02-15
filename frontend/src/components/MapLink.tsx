import { MapPin } from 'lucide-react'
import { Button } from './ui/button'

interface MapLinkProps {
  latitude: number | null
  longitude: number | null
  locationName: string
  className?: string
  variant?: 'default' | 'outline' | 'ghost' | 'link'
  size?: 'default' | 'sm' | 'lg' | 'icon'
}

/**
 * Opens the location in the appropriate maps app.
 * iOS devices → Apple Maps
 * Other devices → Google Maps
 */
function getMapUrl(latitude: number, longitude: number, locationName: string): string {
  const isIOS = /iPad|iPhone|iPod/.test(navigator.userAgent)

  if (isIOS) {
    return `https://maps.apple.com/?ll=${latitude},${longitude}&q=${encodeURIComponent(locationName)}`
  }

  return `https://www.google.com/maps?q=${latitude},${longitude}`
}

export function MapLink({
  latitude,
  longitude,
  locationName,
  className,
  variant = 'outline',
  size = 'sm',
}: MapLinkProps) {
  if (latitude === null || longitude === null) {
    return null
  }

  const mapUrl = getMapUrl(latitude, longitude, locationName)

  return (
    <Button
      variant={variant}
      size={size}
      className={className}
      asChild
    >
      <a
        href={mapUrl}
        target="_blank"
        rel="noopener noreferrer"
        aria-label={`${locationName} auf Karte anzeigen`}
      >
        <MapPin className="h-4 w-4 mr-1" />
        Karte
      </a>
    </Button>
  )
}
