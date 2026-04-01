import { getMenuItems, getRestaurant } from "../../../lib/api";
import SubmissionForm from "./submission-form";



function groupByCategory(items: any[]) {
    return items.reduce((acc: Record<string, any[]>, item: any) => {
        const key = item.category || "Diğer";

        if (!acc[key]) {
            acc[key] = [];
        }

        acc[key].push(item);
        return acc;
    }, {});
}

export default async function RestaurantDetail({ params }: { params: { id: string } }) {
    const restaurant = await getRestaurant(params.id);
    const items = await getMenuItems(params.id);
    const groupedItems = groupByCategory(items);
    return (

        <main className="container">
            <a className="back-link" href="/">← Geri</a>

            <div className="card hero">
                <h1 className="page-title">{restaurant.name}</h1>
                <p className="page-subtitle">{restaurant.address}</p>

                <div className="badges">
                    <span className="badge">{restaurant.cuisineType}</span>
                    <span className="badge">
            {restaurant.verified ? "Restoran doğruladı" : "Henüz doğrulanmadı"}
          </span>
                </div>
            </div>

            <div className="card">
                <h2 className="section-title">Menü</h2>

                {Object.entries(groupedItems).map(([category, categoryItems]) => (
                    <div className="category-block" key={category}>
                        <div className="category-title">{category}</div>

                        <div className="menu-list">
                            {(categoryItems as any[]).map((item: any) => (
                                <div className="menu-item" key={item.id}>
                                    <div className="row-between">
                                        <div>
                                            <div className="menu-item-name">{item.name}</div>
                                            {item.createdAt ? (
                                                <div className="menu-item-category">
                                                    Eklenme: {new Date(item.createdAt).toLocaleString("tr-TR")}
                                                </div>
                                            ) : null}
                                        </div>

                                        <div className="price">
                                            {item.priceAmount} {item.currency}
                                        </div>
                                    </div>
                                </div>
                            ))}
                        </div>
                    </div>
                ))}
            </div>

            <div className="card">
                <h2 className="section-title">Menü bilgisi veya yalnızca ürün bilgisi öner</h2>
                <SubmissionForm restaurantId={Number(params.id)} />
            </div>
        </main>

    );
}
