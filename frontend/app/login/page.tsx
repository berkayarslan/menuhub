"use client";

import { useState } from "react";
import { login } from "../../lib/api";
import { useRouter } from "next/navigation";

export default function LoginPage() {
    const router = useRouter();
    const [username, setUsername] = useState("admin");
    const [password, setPassword] = useState("admin123");
    const [error, setError] = useState("");

    async function onSubmit(e: React.FormEvent) {
        e.preventDefault();
        setError("");

        try {
            const data = await login({ username, password });
            localStorage.setItem("admin_token", data.token);
            router.push("/admin/submissions");
        } catch {
            setError("Giriş başarısız");
        }
    }

    return (
        <main className="container">
            <div className="card" style={{ maxWidth: 480, margin: "60px auto" }}>
                <h1 className="page-title" style={{ fontSize: 32 }}>Admin Giriş</h1>
                <p className="page-subtitle">Moderasyon paneline erişmek için giriş yap.</p>

                <form onSubmit={onSubmit} style={{ display: "grid", gap: 12 }}>
                    <input
                        className="input"
                        placeholder="Kullanıcı adı"
                        value={username}
                        onChange={(e) => setUsername(e.target.value)}
                    />

                    <input
                        className="input"
                        type="password"
                        placeholder="Şifre"
                        value={password}
                        onChange={(e) => setPassword(e.target.value)}
                    />

                    <button className="button" type="submit">Giriş yap</button>

                    {error ? <p className="status-text">{error}</p> : null}
                </form>
            </div>
        </main>
    );
}