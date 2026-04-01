"use client";

import { useEffect, useState } from "react";
import { useRouter } from "next/navigation";

type Restaurant = {
    id: number;
    name: string;
    city: string;
    district: string;
    address: string;
    cuisineType: string;
    verified: boolean;
    deleted?: boolean;
    deletedAt?: string;
};

export default function AdminRestaurantsPage() {
    const router = useRouter();
    const [restaurants, setRestaurants] = useState<Restaurant[]>([]);
    const [loading, setLoading] = useState(true);

    async function loadRestaurants() {
        const token = localStorage.getItem("admin_token");
        if (!token) {
            router.push("/login");
            return;
        }

        const res = await fetch("http://localhost:8080/api/admin/restaurants", {
            headers: {
                Authorization: `Bearer ${token}`
            },
            cache: "no-store"
        });

        const data = await res.json();
        setRestaurants(data || []);
        setLoading(false);
    }

    useEffect(() => {
        loadRestaurants();
    }, []);

    async function onSoftDelete(id: number) {
        const token = localStorage.getItem("admin_token");

        await fetch(`http://localhost:8080/api/admin/restaurants/${id}/soft-delete`, {
            method: "POST",
            headers: {
                Authorization: `Bearer ${token}`
            }
        });

        await loadRestaurants();
    }

    async function onRestore(id: number) {
        const token = localStorage.getItem("admin_token");

        await fetch(`http://localhost:8080/api/admin/restaurants/${id}/restore`, {
            method: "POST",
            headers: {
                Authorization: `Bearer ${token}`
            }
        });

        await loadRestaurants();
    }

    if (loading) {
        return (
            <main className="container">
                <div className="card">Yükleniyor...</div>
            </main>
        );
    }

    return (
        <main className="container">
            <div className="row-between" style={{ marginBottom: 16 }}>
                <div>
                    <h1 className="page-title">Admin · Restoranlar</h1>
                    <p className="page-subtitle">Restoran ekle, düzenle, gizle veya geri aç.</p>
                </div>

                <button
                    className="button"
                    onClick={() => {
                        localStorage.removeItem("admin_token");
                        router.push("/login");
                    }}
                >
                    Çıkış
                </button>
            </div>

            <p style={{ marginBottom: 24 }}>
                <a className="back-link" href="/admin/submissions">← Moderasyon paneli</a>
            </p>

            <div className="card">
                <a className="button" href="/admin/restaurants/new">Yeni restoran ekle</a>
            </div>

            {restaurants.map((restaurant) => (
                <div
                    key={restaurant.id}
                    className="card restaurant-link-card"
                    style={{
                        opacity: restaurant.deleted ? 0.65 : 1,
                        borderColor: restaurant.deleted ? "#7f1d1d" : undefined
                    }}
                >
                    <div className="row-between">
                        <div>
                            <div style={{ fontSize: 22, fontWeight: 800, display: "flex", gap: 10, alignItems: "center" }}>
                                <span>{restaurant.name}</span>
                                {restaurant.deleted ? <span title="Kullanıcılara gizli">🚫</span> : null}
                            </div>

                            <p style={{ color: "#cbd5e1", marginBottom: 8 }}>
                                {restaurant.district} / {restaurant.city}
                            </p>

                            <small style={{ color: "#94a3b8" }}>{restaurant.address}</small>

                            <div className="badges" style={{ marginTop: 12 }}>
                                <span className="badge">{restaurant.cuisineType}</span>
                                <span className="badge">{restaurant.verified ? "Doğrulandı" : "Doğrulanmadı"}</span>
                                {restaurant.deleted ? <span className="badge">Soft deleted</span> : null}
                            </div>
                        </div>

                        <div style={{ display: "flex", gap: 10, flexWrap: "wrap" }}>
                            <a className="button" href={`/admin/restaurants/${restaurant.id}/edit`}>Düzenle</a>
                            <a className="button" href={`/admin/restaurants/${restaurant.id}/menu`}>Menü</a>

                            {!restaurant.deleted ? (
                                <button className="button" type="button" onClick={() => onSoftDelete(restaurant.id)}>
                                    Gizle
                                </button>
                            ) : (
                                <button className="button" type="button" onClick={() => onRestore(restaurant.id)}>
                                    Geri aç
                                </button>
                            )}
                        </div>
                    </div>
                </div>
            ))}
        </main>
    );
}