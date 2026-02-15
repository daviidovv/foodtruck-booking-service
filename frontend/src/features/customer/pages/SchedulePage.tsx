import { useQuery } from '@tanstack/react-query'
import { api } from '@/lib/api'
import { MapLink } from '@/components/MapLink'
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'
import { Clock, MapPin } from 'lucide-react'

function formatTime(time: string): string {
  return time.substring(0, 5)
}

export function SchedulePage() {
  const { data, isLoading, error } = useQuery({
    queryKey: ['weeklySchedule'],
    queryFn: () => api.getWeeklySchedule(),
    staleTime: 5 * 60 * 1000, // 5 minutes
  })

  if (isLoading) {
    return (
      <div className="container mx-auto px-4 py-8">
        <div className="text-center py-12">
          <div className="animate-pulse">Lade Wochenplan...</div>
        </div>
      </div>
    )
  }

  if (error) {
    return (
      <div className="container mx-auto px-4 py-8">
        <div className="text-center py-12 text-red-600">
          Fehler beim Laden des Wochenplans
        </div>
      </div>
    )
  }

  const daysWithSchedule = data?.schedule || []
  const allDays = [
    { dayOfWeek: 1, dayName: 'Montag' },
    { dayOfWeek: 2, dayName: 'Dienstag' },
    { dayOfWeek: 3, dayName: 'Mittwoch' },
    { dayOfWeek: 4, dayName: 'Donnerstag' },
    { dayOfWeek: 5, dayName: 'Freitag' },
    { dayOfWeek: 6, dayName: 'Samstag' },
    { dayOfWeek: 7, dayName: 'Sonntag' },
  ]

  const scheduleByDay = new Map(
    daysWithSchedule.map((day) => [day.dayOfWeek, day])
  )

  return (
    <div className="container mx-auto px-4 py-8">
      <div className="max-w-2xl mx-auto">
        <h1 className="text-3xl font-bold text-center mb-2">Wochenplan</h1>
        <p className="text-muted-foreground text-center mb-8">
          Hier findest du uns diese Woche
        </p>

        <div className="space-y-4">
          {allDays.map((day) => {
            const daySchedule = scheduleByDay.get(day.dayOfWeek)

            if (!daySchedule) {
              return (
                <Card key={day.dayOfWeek} className="opacity-60">
                  <CardHeader className="py-4">
                    <CardTitle className="text-lg flex items-center justify-between">
                      <span>{day.dayName}</span>
                      <span className="text-sm font-normal text-muted-foreground">
                        Ruhetag
                      </span>
                    </CardTitle>
                  </CardHeader>
                </Card>
              )
            }

            return (
              <Card key={day.dayOfWeek}>
                <CardHeader className="py-4 pb-2">
                  <CardTitle className="text-lg">{daySchedule.dayName}</CardTitle>
                </CardHeader>
                <CardContent className="pt-0">
                  <div className="space-y-3">
                    {daySchedule.locations.map((location) => (
                      <div
                        key={location.locationId}
                        className="flex items-center justify-between py-2 border-b last:border-0"
                      >
                        <div className="flex-1">
                          <div className="font-medium">{location.locationName}</div>
                          <div className="text-sm text-muted-foreground flex items-center gap-4">
                            <span className="flex items-center gap-1">
                              <Clock className="h-3 w-3" />
                              {formatTime(location.openingTime)} - {formatTime(location.closingTime)} Uhr
                            </span>
                            {location.address && (
                              <span className="flex items-center gap-1">
                                <MapPin className="h-3 w-3" />
                                {location.address}
                              </span>
                            )}
                          </div>
                        </div>
                        <MapLink
                          latitude={location.latitude}
                          longitude={location.longitude}
                          locationName={location.locationName}
                        />
                      </div>
                    ))}
                  </div>
                </CardContent>
              </Card>
            )
          })}
        </div>
      </div>
    </div>
  )
}
