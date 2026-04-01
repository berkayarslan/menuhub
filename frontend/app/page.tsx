import Link from "next/link";
import { getRestaurants } from "../lib/api";

export default async function Home() {
    const restaurants = await getRestaurants();

    return (
        <main className="container">
            <h1 className="page-title">MenuHub</h1>
            <p style={{ marginBottom: 24 }}>
                <a className="back-link" href="/login">Admin giriş</a>
            </p>
            <p className="page-subtitle">
                Text tabanlı restoran menü ve fiyat keşif platformu
            </p>

            {restaurants.map((r: any) => (
                <Link href={`/restaurants/${r.id}`} key={r.id}>
                    <div className="card restaurant-link-card">
                        <div className="row-between">
                            <strong style={{ fontSize: 22 }}>{r.name}</strong>
                            <span className="badge">
                {r.verified ? "Doğrulanmış" : "Topluluk verisi"}
              </span>
                        </div>

                        <p style={{ color: "#cbd5e1", marginBottom: 8 }}>
                            {r.district} / {r.city}
                        </p>

                        <small style={{ color: "#94a3b8" }}>{r.cuisineType}</small>
                    </div>
                </Link>
            ))}
        </main>
    );
}