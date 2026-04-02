"use client";

import { useEffect, useState } from "react";
import { useRouter } from "next/navigation";
import SubmissionActions from "./actions";

type Submission = {
    id: number;
    restaurantId: number;
    sourceType: string;
    rawText: string;
    status: string;
    createdAt?: string;
    restaurantName?: string;
};

export default function AdminSubmissionsPage() {
    const router = useRouter();
    const [submissions, setSubmissions] = useState<Submission[]>([]);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        const token = localStorage.getItem("admin_token");

        if (!token) {
            router.push("/login");
            return;
        }

        fetch("http://localhost:8080/api/admin/submissions", {
            headers: {
                Authorization: `Bearer ${token}`
            },
            cache: "no-store"
        })
            .then(async (res) => {
                if (res.status === 401 || res.status === 403) {
                    localStorage.removeItem("admin_token");
                    router.push("/login");
                    return [];
                }

                if (!res.ok) {
                    throw new Error("Failed to fetch submissions");
                }

                return res.json();
            })
            .then((data) => {
                setSubmissions(data || []);
            })
            .catch((err) => {
                console.error(err);
            })
            .finally(() => {
                setLoading(false);
            });
    }, [router]);

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
                    <h1 className="page-title">Admin Moderasyon</h1>
                    <p className="page-subtitle">Menü katkılarını incele, onayla veya reddet.</p>
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
                <a className="back-link" href="/admin/restaurants">Restoran bazlı menü yönetimi →</a>
            </p>

            {submissions.length === 0 ? (
                <div className="card">
                    <p>Henüz bekleyen yok.</p>
                </div>
            ) : (
                submissions.map((submission) => (
                    <div className="card" key={submission.id}>
                        <div className="row-between" style={{ marginBottom: 14 }}>
                            <div>
                                <div style={{ fontSize: 22, fontWeight: 800 }}>Katkı #{submission.id}</div>
                                <div style={{ color: "#94a3b8", marginTop: 6 }}>
                                    {submission.restaurantName ? `Restoran ADI: ${submission.restaurantName} · ` : ""}
                                    Restaurant ID: {submission.restaurantId} · Kaynak: {submission.sourceType}
                                </div>
                            </div>

                            <span className="badge">{submission.status}</span>
                        </div>

                        <div
                            style={{
                                whiteSpace: "pre-wrap",
                                background: "#081120",
                                border: "1px solid #1e293b",
                                borderRadius: 12,
                                padding: 16,
                                marginBottom: 16,
                                color: "#cbd5e1"
                            }}
                        >
                            {submission.rawText}
                        </div>

                        {submission.status === "PENDING_REVIEW" ? (
                            <SubmissionActions id={submission.id} />
                        ) : (
                            <p className="status-text">Bu katkı işlem görmüş.</p>
                        )}
                    </div>
                ))
            )}
        </main>
    );
}