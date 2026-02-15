// Location types
export interface Location {
  id: string
  name: string
  address: string
  latitude: number | null
  longitude: number | null
  active: boolean
  createdAt: string
  updatedAt: string
}

export interface Schedule {
  id: string
  dayOfWeek: number
  dayName: string
  openingTime: string
  closingTime: string
  dailyCapacity: number
  active: boolean
}

export interface LocationWithSchedule extends Location {
  schedules: Schedule[]
}

// Weekly schedule types
export interface LocationScheduleEntry {
  locationId: string
  locationName: string
  address: string
  latitude: number | null
  longitude: number | null
  openingTime: string
  closingTime: string
}

export interface DaySchedule {
  dayOfWeek: number
  dayName: string
  locations: LocationScheduleEntry[]
}

export interface WeeklySchedule {
  schedule: DaySchedule[]
}

// Availability types
export type AvailabilityStatus =
  | 'AVAILABLE'
  | 'LIMITED'
  | 'ALMOST_FULL'
  | 'SOLD_OUT'
  | 'NOT_AVAILABLE'
  | 'CLOSED'

export interface Availability {
  locationId: string
  locationName: string
  date: string
  dayOfWeek: number
  dayName: string
  openingTime: string | null
  closingTime: string | null
  inventorySet: boolean
  totalChickens: number | null
  reservedChickens: number | null
  availableChickens: number
  isOpen: boolean
  availabilityStatus: AvailabilityStatus
  message?: string
}

// Reservation types
export type ReservationStatus =
  | 'PENDING'
  | 'CONFIRMED'
  | 'CANCELLED'
  | 'COMPLETED'
  | 'NO_SHOW'

export interface Reservation {
  id: string
  confirmationCode: string
  locationId: string
  locationName: string
  locationAddress: string
  customerName: string
  customerEmail: string | null
  chickenCount: number
  friesCount: number
  reservationDate: string
  pickupTime: string | null
  status: ReservationStatus
  notes: string | null
  createdAt: string
  updatedAt: string
  canCancel: boolean
  message?: string
}

export interface CreateReservationRequest {
  locationId: string
  customerName: string
  customerEmail?: string
  chickenCount: number
  friesCount: number
  pickupTime?: string
  notes?: string
}

// Inventory types
export interface Inventory {
  id: string | null
  locationId: string
  locationName: string
  date: string
  inventorySet: boolean
  totalChickens: number | null
  reservedChickens: number | null
  availableChickens: number | null
  reservationCount: number | null
  utilizationPercent: number | null
  status: AvailabilityStatus | null
  message?: string
}

// API Response types
export interface ApiError {
  type: string
  title: string
  status: number
  detail: string
  instance: string
  timestamp: string
  errors?: { field: string; message: string }[]
}

export interface PaginatedResponse<T> {
  content: T[]
  totalElements: number
  totalPages: number
  size: number
  number: number
  first: boolean
  last: boolean
}
