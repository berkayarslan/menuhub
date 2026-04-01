"use client";

import { useMemo, useState } from "react";
import { createSubmission } from "../../../lib/api";

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

function detectSuggestedCategory(name: string): string | null {
    const value = name.toLocaleLowerCase("tr-TR").trim();

    if (!value) return null;

    if (
        value.includes("kebap") ||
        value.includes("adana") ||
        value.includes("urfa") ||
        value.includes("şiş") ||
        value.includes("sis") ||
        value.includes("kanat")
    ) {
        return "Kebaplar";
    }

    if (
        value.includes("ayran") ||
        value.includes("kola") ||
        value.includes("cola") ||
        value.includes("fanta") ||
        value.includes("su") ||
        value.includes("çay") ||
        value.includes("cay") ||
        value.includes("kahve")
    ) {
        return "İçecekler";
    }

    if (
        value.includes("künefe") ||
        value.includes("kunefe") ||
        value.includes("sütlaç") ||
        value.includes("sutlac") ||
        value.includes("baklava") ||
        value.includes("havuc") ||
        value.includes("havuç") ||
        value.includes("kadayıf") ||
        value.includes("kadayif") ||
        value.includes("tatlı") ||
        value.includes("tatli")
    ) {
        return "Tatlılar";
    }

    if (value.includes("çorba") || value.includes("corba")) {
        return "Çorbalar";
    }

    if (value.includes("salata")) {
        return "Salatalar";
    }

    if (value.includes("pide")) {
        return "Pideler";
    }

    if (value.includes("lahmacun")) {
        return "Lahmacunlar";
    }

    if (value.includes("pizza")) {
        return "Pizzalar";
    }

    if (value.includes("burger")) {
        return "Burgerler";
    }

    if (value.includes("döner") || value.includes("doner")) {
        return "Dönerler";
    }

    return null;
}

export default function SubmissionForm({ restaurantId }: { restaurantId: number }) {
    const [category, setCategory] = useState("");
    const [name, setName] = useState("");
    const [priceAmount, setPriceAmount] = useState("");
    const [currency, setCurrency] = useState("TRY");
    const [status, setStatus] = useState("");

    const suggestedCategory = useMemo(() => detectSuggestedCategory(name), [name]);

    const categoryWarning = useMemo(() => {
        if (!name || !category || !suggestedCategory) return "";
        if (category !== suggestedCategory) {
            return `Uyarı: "${name}" için önerilen kategori "${suggestedCategory}" görünüyor. Yine de gönderebilirsin.`;
        }
        return "";
    }, [name, category, suggestedCategory]);

    async function onSubmit(e: React.FormEvent) {
        e.preventDefault();
        setStatus("Gönderiliyor...");

        try {
            const rawText = `${category} | ${name} | ${priceAmount} ${currency}`;

            await createSubmission({
                restaurantId,
                sourceType: "structured_form",
                rawText
            });

            setCategory("");
            setName("");
            setPriceAmount("");
            setCurrency("TRY");
            setStatus("Katkı alındı. Admin onayına gönderildi.");
        } catch {
            setStatus("Bir hata oluştu.");
        }
    }

    return (
        <form onSubmit={onSubmit} style={{ display: "grid", gap: 12 }}>
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

            <div style={{ display: "grid", gridTemplateColumns: "1fr 160px", gap: 12 }}>
                <input
                    className="input compact-input"
                    type="number"
                    step="0.01"
                    placeholder="Fiyat"
                    value={priceAmount}
                    onChange={(e) => setPriceAmount(e.target.value)}
                />

                <select
                    className="input compact-input"
                    value={currency}
                    onChange={(e) => setCurrency(e.target.value)}
                >
                    <option value="TRY">TRY</option>
                    <option value="USD">USD</option>
                    <option value="EUR">EUR</option>
                </select>
            </div>

            {categoryWarning ? (
                <div
                    style={{
                        background: "#3a2a00",
                        color: "#fde68a",
                        border: "1px solid #92400e",
                        padding: 12,
                        borderRadius: 10
                    }}
                >
                    {categoryWarning}
                </div>
            ) : null}

            <button className="button" type="submit">
                Gönder
            </button>

            {status ? <p className="status-text">{status}</p> : null}
        </form>
    );
}