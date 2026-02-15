import type {
  Location,
  LocationWithSchedule,
  Availability,
  Reservation,
  CreateReservationRequest,
  Inventory,
  ApiError,
  WeeklySchedule,
} from '@/types'

const API_BASE = '/api/v1'

class ApiClient {
  private async request<T>(
    endpoint: string,
    options: RequestInit = {}
  ): Promise<T> {
    const url = `${API_BASE}${endpoint}`

    const response = await fetch(url, {
      headers: {
        'Content-Type': 'application/json',
        ...options.headers,
      },
      ...options,
    })

    if (!response.ok) {
      const error: ApiError = await response.json()
      throw error
    }

    return response.json()
  }

  private requestWithAuth<T>(
    endpoint: string,
    username: string,
    password: string,
    options: RequestInit = {}
  ): Promise<T> {
    const credentials = btoa(`${username}:${password}`)
    return this.request<T>(endpoint, {
      ...options,
      headers: {
        ...options.headers,
        Authorization: `Basic ${credentials}`,
      },
    })
  }

  // Public endpoints
  async getLocations(): Promise<{ content: Location[] }> {
    return this.request('/locations')
  }

  async getTodayLocations(): Promise<{ content: Location[] }> {
    return this.request('/locations/today')
  }

  async getWeeklySchedule(): Promise<WeeklySchedule> {
    return this.request('/schedule')
  }

  async getLocationSchedule(locationId: string): Promise<LocationWithSchedule> {
    return this.request(`/locations/${locationId}/schedule`)
  }

  async getAvailability(locationId: string, date?: string): Promise<Availability> {
    const params = date ? `?date=${date}` : ''
    return this.request(`/locations/${locationId}/availability${params}`)
  }

  async createReservation(data: CreateReservationRequest): Promise<Reservation> {
    return this.request('/reservations', {
      method: 'POST',
      body: JSON.stringify(data),
    })
  }

  async getReservationByCode(code: string): Promise<Reservation> {
    return this.request(`/reservations/code/${code}`)
  }

  async cancelReservation(code: string): Promise<Reservation> {
    return this.request(`/reservations/code/${code}`, {
      method: 'DELETE',
    })
  }

  // Staff endpoints (require auth)
  async getStaffReservations(
    locationId: string,
    date: string,
    username: string,
    password: string
  ): Promise<Reservation[]> {
    return this.requestWithAuth(
      `/staff/reservations?locationId=${locationId}&date=${date}`,
      username,
      password
    )
  }

  async getInventory(
    locationId: string,
    username: string,
    password: string,
    date?: string
  ): Promise<Inventory> {
    const params = date ? `&date=${date}` : ''
    return this.requestWithAuth(
      `/staff/inventory?locationId=${locationId}${params}`,
      username,
      password
    )
  }

  async setInventory(
    locationId: string,
    totalChickens: number,
    username: string,
    password: string
  ): Promise<Inventory> {
    return this.requestWithAuth(
      '/staff/inventory',
      username,
      password,
      {
        method: 'POST',
        body: JSON.stringify({ locationId, totalChickens }),
      }
    )
  }

  async updateReservationStatus(
    reservationId: string,
    status: string,
    username: string,
    password: string,
    notes?: string
  ): Promise<Reservation> {
    return this.requestWithAuth(
      `/staff/reservations/${reservationId}/status`,
      username,
      password,
      {
        method: 'PATCH',
        body: JSON.stringify({ status, notes }),
      }
    )
  }

  // Admin endpoints (require ADMIN role)
  async verifyAdmin(username: string, password: string): Promise<{ content: Reservation[] }> {
    // Verify admin credentials by calling an admin-only endpoint
    return this.requestWithAuth('/admin/reservations?size=1', username, password)
  }

  async getAdminReservations(
    date: string,
    username: string,
    password: string,
    locationId?: string
  ): Promise<Reservation[]> {
    const locationParam = locationId ? `&locationId=${locationId}` : ''
    const response = await this.requestWithAuth<{ content: Reservation[] }>(
      `/admin/reservations?date=${date}${locationParam}&size=100`,
      username,
      password
    )
    return response.content
  }

  async createLocation(
    name: string,
    address: string,
    username: string,
    password: string
  ): Promise<Location> {
    return this.requestWithAuth(
      '/admin/locations',
      username,
      password,
      {
        method: 'POST',
        body: JSON.stringify({ name, address }),
      }
    )
  }

  async updateLocation(
    locationId: string,
    name: string,
    address: string,
    active: boolean,
    username: string,
    password: string
  ): Promise<Location> {
    return this.requestWithAuth(
      `/admin/locations/${locationId}`,
      username,
      password,
      {
        method: 'PUT',
        body: JSON.stringify({ name, address, active }),
      }
    )
  }
}

export const api = new ApiClient()
