"use client";

import { useState } from "react";
import { createMenuItem } from "../../../lib/api";

export default function AddMenuItemForm({ restaurantId }: { restaurantId: number }) {
    const [category, setCategory] = useState("");
    const [name, setName] = useState("");
    const [descriptionText, setDescriptionText] = useState("");
    const [priceAmount, setPriceAmount] = useState("");
    const [currency, setCurrency] = useState("TRY");
    const [status, setStatus] = useState("");

    async function onSubmit(e: React.FormEvent) {
        e.preventDefault();
        setStatus("Kaydediliyor...");

        try {
            await createMenuItem(restaurantId, {
                category,
                name,
                descriptionText,
                priceAmount: Number(priceAmount),
                currency
            });

            setCategory("");
            setName("");
            setDescriptionText("");
            setPriceAmount("");
            setCurrency("TRY");
            setStatus("Menüye eklendi. Sayfa yenileniyor...");
            window.location.reload();
        } catch {
            setStatus("Bir hata oluştu.");
        }
    }

    return (
        <form onSubmit={onSubmit}>
            <div style={{ display: "grid", gap: 12 }}>
                <input
                    className="input"
                    placeholder="Kategori (örn: Kebaplar)"
                    value={category}
                    onChange={(e) => setCategory(e.target.value)}
                />

                <input
                    className="input"
                    placeholder="Ürün adı (örn: Tavuk Şiş)"
                    value={name}
                    onChange={(e) => setName(e.target.value)}
                />

                <textarea
                    className="textarea"
                    placeholder="Açıklama (opsiyonel)"
                    value={descriptionText}
                    onChange={(e) => setDescriptionText(e.target.value)}
                />

                <input
                    className="input"
                    type="number"
                    step="0.01"
                    placeholder="Fiyat"
                    value={priceAmount}
                    onChange={(e) => setPriceAmount(e.target.value)}
                />

                <input
                    className="input"
                    placeholder="Para birimi"
                    value={currency}
                    onChange={(e) => setCurrency(e.target.value)}
                />

                <button className="button" type="submit">
                    Menüye ekle
                </button>

                {status ? <p className="status-text">{status}</p> : null}
            </div>
        </form>
    );
}