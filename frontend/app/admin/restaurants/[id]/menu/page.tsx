"use client";

import { useEffect, useState } from "react";
import { useRouter, useParams } from "next/navigation";

type Restaurant = {
    id: number;
    name: string;
    city: string;
    district: string;
    address: string;
    cuisineType: string;
    verified: boolean;
};

type MenuItem = {
    id: number;
    category: string;
    name: string;
    descriptionText?: string;
    priceAmount: number;
    currency: string;
    createdAt?: string;
    updatedAt?: string;
};

function groupByCategory(items: MenuItem[]) {
    return items.reduce((acc: Record<string, MenuItem[]>, item: MenuItem) => {
        const key = item.category || "Diğer";
        if (!acc[key]) acc[key] = [];
        acc[key].push(item);
        return acc;
    }, {});
}

const CATEGORY_OPTIONS = [
    "Kebaplar",
    "Izgaralar",
    "Dönerler",
    "Pideler",
    "Lahmacunlar",
    "Burgerler",
    "Pizzalar",
    "Çorbalar",
    "Salatalar",
    "Ara Sıcaklar",
    "Tatlılar",
    "İçecekler"
];

export default function AdminRestaurantMenuPage() {
    const router = useRouter();
    const params = useParams();
    const restaurantId = Number(params.id);

    const [restaurant, setRestaurant] = useState<Restaurant | null>(null);
    const [items, setItems] = useState<MenuItem[]>([]);
    const [loading, setLoading] = useState(true);

    const [category, setCategory] = useState("");
    const [name, setName] = useState("");
    const [descriptionText, setDescriptionText] = useState("");
    const [priceAmount, setPriceAmount] = useState("");
    const [currency, setCurrency] = useState("TRY");
    const [status, setStatus] = useState("");

    const [editingItemId, setEditingItemId] = useState<number | null>(null);

    const groupedItems = groupByCategory(items);

    async function loadData() {
        const token = localStorage.getItem("admin_token");
        if (!token) {
            router.push("/login");
            return;
        }

        const [restaurantRes, itemsRes] = await Promise.all([
            fetch(`http://localhost:8080/api/restaurants/${restaurantId}`, { cache: "no-store" }),
            fetch(`http://localhost:8080/api/restaurants/${restaurantId}/menu-items`, { cache: "no-store" })
        ]);

        const restaurantData = await restaurantRes.json();
        const itemsData = await itemsRes.json();

        setRestaurant(restaurantData);
        setItems(itemsData || []);
        setLoading(false);
    }

    useEffect(() => {
        loadData();
    }, [restaurantId]);

    function resetForm() {
        setCategory("");
        setName("");
        setDescriptionText("");
        setPriceAmount("");
        setCurrency("TRY");
        setEditingItemId(null);
    }

    function startEdit(item: MenuItem) {
        setEditingItemId(item.id);
        setCategory(item.category || "");
        setName(item.name || "");
        setDescriptionText(item.descriptionText || "");
        setPriceAmount(String(item.priceAmount ?? ""));
        setCurrency(item.currency || "TRY");
        setStatus("");
        window.scrollTo({ top: document.body.scrollHeight, behavior: "smooth" });
    }

    async function onDelete(menuItemId: number) {
        const token = localStorage.getItem("admin_token");
        if (!confirm("Bu ürünü silmek istediğine emin misin?")) return;

        const res = await fetch(
            `http://localhost:8080/api/admin/restaurants/${restaurantId}/menu-items/${menuItemId}`,
            {
                method: "DELETE",
                headers: {
                    Authorization: `Bearer ${token}`
                }
            }
        );

        if (res.ok) {
            await loadData();
        }
    }

    async function onSubmit(e: React.FormEvent) {
        e.preventDefault();
        setStatus(editingItemId ? "Güncelleniyor..." : "Kaydediliyor...");

        try {
            const token = localStorage.getItem("admin_token");

            const payload = {
                category,
                name,
                descriptionText,
                priceAmount: Number(priceAmount),
                currency
            };

            const url = editingItemId
                ? `http://localhost:8080/api/admin/restaurants/${restaurantId}/menu-items/${editingItemId}`
                : `http://localhost:8080/api/admin/restaurants/${restaurantId}/menu-items`;

            const method = editingItemId ? "PUT" : "POST";

            const res = await fetch(url, {
                method,
                headers: {
                    "Content-Type": "application/json",
                    Authorization: `Bearer ${token}`
                },
                body: JSON.stringify(payload)
            });

            if (!res.ok) throw new Error();

            resetForm();
            setStatus(editingItemId ? "Ürün güncellendi" : "Ürün eklendi");
            await loadData();
        } catch {
            setStatus("Bir hata oluştu");
        }
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
            <p style={{ marginBottom: 24 }}>
                <a className="back-link" href="/admin/restaurants">← Restoran listesine dön</a>
            </p>

            <div className="card">
                <div className="row-between">
                    <div>
                        <h1 className="page-title">{restaurant?.name}</h1>
                        <p className="page-subtitle">{restaurant?.address}</p>
                        <div className="badges">
                            <span className="badge">{restaurant?.cuisineType}</span>
                            <span className="badge">Admin menü yönetimi</span>
                        </div>
                    </div>

                    <a className="button" href={`/admin/restaurants/${restaurantId}/edit`}>
                        Restoran bilgilerini düzenle
                    </a>
                </div>
            </div>

            <div className="card">
                <h2 className="section-title">Mevcut menü</h2>

                {Object.entries(groupedItems).map(([categoryName, categoryItems]) => (
                    <div className="category-block" key={categoryName}>
                        <div className="category-title">{categoryName}</div>

                        <div className="menu-list">
                            {(categoryItems as MenuItem[]).map((item) => (
                                <div className="menu-item" key={item.id}>
                                    <div className="row-between">
                                        <div>
                                            <div className="menu-item-name">{item.name}</div>

                                            <div className="menu-item-category" style={{ marginTop: 6 }}>
                                                Admin ekleme tarihi:{" "}
                                                {item.createdAt ? new Date(item.createdAt).toLocaleString("tr-TR") : "-"}
                                            </div>

                                            {item.updatedAt ? (
                                                <div className="menu-item-category">
                                                    Son güncelleme: {new Date(item.updatedAt).toLocaleString("tr-TR")}
                                                </div>
                                            ) : null}
                                        </div>

                                        <div style={{ textAlign: "right" }}>
                                            <div className="price">
                                                {item.priceAmount} {item.currency}
                                            </div>

                                            <div style={{ display: "flex", gap: 8, justifyContent: "flex-end", marginTop: 10 }}>
                                                <button className="button" type="button" onClick={() => startEdit(item)}>
                                                    Düzenle
                                                </button>

                                                <button
                                                    type="button"
                                                    onClick={() => onDelete(item.id)}
                                                    style={{
                                                        padding: "12px 18px",
                                                        border: "1px solid #7f1d1d",
                                                        borderRadius: 12,
                                                        background: "#3f0d0d",
                                                        color: "white",
                                                        cursor: "pointer",
                                                        fontWeight: 700
                                                    }}
                                                >
                                                    Sil
                                                </button>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            ))}
                        </div>
                    </div>
                ))}
            </div>

            <div className="card">
                <h2 className="section-title">
                    {editingItemId ? "Ürünü düzenle" : "Yeni ürün ekle"}
                </h2>

                <form onSubmit={onSubmit} style={{ display: "grid", gap: 10 }}>
                    <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr", gap: 10 }}>
                        <select
                            className="input compact-input"
                            value={category}
                            onChange={(e) => setCategory(e.target.value)}
                        >
                            <option value="">Kategori seç</option>
                            {CATEGORY_OPTIONS.map((option) => (
                                <option key={option} value={option}>
                                    {option}
                                </option>
                            ))}
                        </select>

                        <input
                            className="input compact-input"
                            placeholder="Ürün adı"
                            value={name}
                            onChange={(e) => setName(e.target.value)}
                        />
                    </div>

                    <textarea
                        className="textarea compact-textarea"
                        placeholder="Açıklama"
                        value={descriptionText}
                        onChange={(e) => setDescriptionText(e.target.value)}
                    />

                    <div style={{ display: "grid", gridTemplateColumns: "1fr 180px", gap: 10 }}>
                        <input
                            className="input compact-input"
                            type="number"
                            step="0.01"
                            placeholder="Fiyat"
                            value={priceAmount}
                            onChange={(e) => setPriceAmount(e.target.value)}
                        />

                        <input
                            className="input compact-input"
                            placeholder="Para birimi"
                            value={currency}
                            onChange={(e) => setCurrency(e.target.value)}
                        />
                    </div>

                    <div style={{ display: "flex", gap: 10 }}>
                        <button className="button" type="submit">
                            {editingItemId ? "Güncelle" : "Ürünü ekle"}
                        </button>

                        {editingItemId ? (
                            <button
                                type="button"
                                onClick={resetForm}
                                style={{
                                    padding: "12px 18px",
                                    border: "1px solid #334155",
                                    borderRadius: 12,
                                    background: "#0f172a",
                                    color: "white",
                                    cursor: "pointer",
                                    fontWeight: 700
                                }}
                            >
                                Vazgeç
                            </button>
                        ) : null}
                    </div>

                    {status ? <p className="status-text">{status}</p> : null}
                </form>
            </div>
        </main>
    );
}