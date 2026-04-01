const API_BASE = "http://localhost:8080/api";
export async function getAdminSubmissions() {
    const res = await fetch(`${API_BASE}/admin/submissions`, { cache: "no-store" });
    if (!res.ok) throw new Error("Failed to fetch admin submissions");
    return res.json();
}

export async function approveSubmission(id: number) {
    const res = await fetch(`${API_BASE}/admin/submissions/${id}/approve`, {
        method: "POST"
    });
    if (!res.ok) throw new Error("Failed to approve submission");
    return res.json();
}

export async function rejectSubmission(id: number) {
    const res = await fetch(`${API_BASE}/admin/submissions/${id}/reject`, {
        method: "POST"
    });
    if (!res.ok) throw new Error("Failed to reject submission");
    return res.json();
}

export async function getRestaurants(q?: string) {
  const url = q ? `${API_BASE}/restaurants?q=${encodeURIComponent(q)}` : `${API_BASE}/restaurants`;
  const res = await fetch(url, { cache: "no-store" });
  if (!res.ok) throw new Error("Failed to fetch restaurants");
  return res.json();
}

export async function login(payload: { username: string; password: string }) {
    const res = await fetch(`${API_BASE}/auth/login`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(payload)
    });

    if (!res.ok) throw new Error("Login failed");
    return res.json();
}

export async function getRestaurant(id: string) {
  const res = await fetch(`${API_BASE}/restaurants/${id}`, { cache: "no-store" });
  if (!res.ok) throw new Error("Failed to fetch restaurant");
  return res.json();
}

export async function getMenuItems(restaurantId: string) {
  const res = await fetch(`${API_BASE}/restaurants/${restaurantId}/menu-items`, { cache: "no-store" });
  if (!res.ok) throw new Error("Failed to fetch menu items");
  return res.json();
}

export async function createSubmission(payload: {
  restaurantId: number;
  sourceType: string;
  rawText: string;
}) {
  const res = await fetch(`${API_BASE}/submissions`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(payload)
  });
  if (!res.ok) throw new Error("Failed to create submission");
  return res.json();
}
export async function createMenuItem(
    restaurantId: number,
    payload: {
        category: string;
        name: string;
        descriptionText?: string;
        priceAmount: number;
        currency: string;
    }
) {
    const res = await fetch(`${API_BASE}/restaurants/${restaurantId}/menu-items`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(payload)
    });

    if (!res.ok) throw new Error("Failed to create menu item");
    return res.json();
}