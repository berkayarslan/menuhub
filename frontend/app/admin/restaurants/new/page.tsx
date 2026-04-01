"use client";

import { useRouter } from "next/navigation";
import { useState } from "react";

export default function NewRestaurantPage() {
    const router = useRouter();

    const [name, setName] = useState("");
    const [city, setCity] = useState("");
    const [district, setDistrict] = useState("");
    const [address, setAddress] = useState("");
    const [cuisineType, setCuisineType] = useState("");
    const [verified, setVerified] = useState(false);
    const [status, setStatus] = useState("");

    async function onSubmit(e: React.FormEvent) {
        e.preventDefault();
        setStatus("Kaydediliyor...");

        try {
            const token = localStorage.getItem("admin_token");

            const res = await fetch("http://localhost:8080/api/admin/restaurants", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                    Authorization: `Bearer ${token}`
                },
                body: JSON.stringify({
                    name,
                    city,
                    district,
                    address,
                    cuisineType,
                    verified
                })
            });

            if (!res.ok) throw new Error();

            router.push("/admin/restaurants");
        } catch {
            setStatus("Bir hata oluştu");
        }
    }

    return (
        <main className="container">
            <p style={{ marginBottom: 24 }}>
                <a className="back-link" href="/admin/restaurants">← Restoran listesine dön</a>
            </p>

            <div className="card">
                <h1 className="page-title">Yeni restoran ekle</h1>

                <form onSubmit={onSubmit} style={{ display: "grid", gap: 12 }}>
                    <input className="input" placeholder="Restoran adı" value={name} onChange={(e) => setName(e.target.value)} />
                    <input className="input" placeholder="Şehir" value={city} onChange={(e) => setCity(e.target.value)} />
                    <input className="input" placeholder="İlçe" value={district} onChange={(e) => setDistrict(e.target.value)} />
                    <input className="input" placeholder="Adres" value={address} onChange={(e) => setAddress(e.target.value)} />
                    <input className="input" placeholder="Mutfak tipi" value={cuisineType} onChange={(e) => setCuisineType(e.target.value)} />

                    <label style={{ display: "flex", gap: 8, alignItems: "center" }}>
                        <input type="checkbox" checked={verified} onChange={(e) => setVerified(e.target.checked)} />
                        Doğrulanmış
                    </label>

                    <button className="button" type="submit">Kaydet</button>
                    {status ? <p className="status-text">{status}</p> : null}
                </form>
            </div>
        </main>
    );
}