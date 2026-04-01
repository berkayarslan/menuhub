"use client";

import { useState } from "react";

export default function SubmissionActions({ id }: { id: number }) {
    const [loading, setLoading] = useState(false);
    const [message, setMessage] = useState("");

    async function onApprove() {
        setLoading(true);
        setMessage("");

        try {
            const token = localStorage.getItem("admin_token");

            const res = await fetch(`http://localhost:8080/api/admin/submissions/${id}/approve`, {
                method: "POST",
                headers: {
                    Authorization: `Bearer ${token}`
                }
            });

            if (!res.ok) {
                throw new Error("Approve failed");
            }

            setMessage("Onaylandı");
            window.location.reload();
        } catch (e) {
            console.error(e);
            setMessage("Onaylama hatası");
        } finally {
            setLoading(false);
        }
    }

    async function onReject() {
        setLoading(true);
        setMessage("");

        try {
            const token = localStorage.getItem("admin_token");

            const res = await fetch(`http://localhost:8080/api/admin/submissions/${id}/reject`, {
                method: "POST",
                headers: {
                    Authorization: `Bearer ${token}`
                }
            });

            if (!res.ok) {
                throw new Error("Reject failed");
            }

            setMessage("Reddedildi");
            window.location.reload();
        } catch (e) {
            console.error(e);
            setMessage("Reddetme hatası");
        } finally {
            setLoading(false);
        }
    }

    return (
        <div style={{ display: "flex", gap: 10, alignItems: "center", flexWrap: "wrap" }}>
            <button className="button" type="button" onClick={onApprove} disabled={loading}>
                Onayla
            </button>

            <button
                type="button"
                onClick={onReject}
                disabled={loading}
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
                Reddet
            </button>

            {message ? <span className="status-text">{message}</span> : null}
        </div>
    );
}