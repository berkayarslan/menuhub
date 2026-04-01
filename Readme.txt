# MenuHub Proje Özeti

## Amaç
Restoranların text tabanlı menü ve fiyat bilgisini yayınlayan sistem.
Admin restoran ve menü yönetebilir. Kullanıcı menüyü görüntüler.

## Stack
- Backend: Spring Boot
- Frontend: Next.js / React
- DB: PostgreSQL
- Infra: Docker
- IDE: IntelliJ

## Mevcut Özellikler
- Restoran listeleme
- Restoran detay sayfası
- Menü listeleme
- Menü ürünlerini kategoriye göre gruplama
- Admin login
- Admin submissions ekranı
- Admin restoran listesi
- Admin restoran oluşturma
- Admin restoran düzenleme
- Restoran soft delete
- Soft deleted restoran kullanıcıya görünmez, admin görür
- Admin restoran menü yönetimi
- Menü ürünü ekleme
- Menü ürünü düzenleme
- Menü ürünü silme
- Menüde createdAt / updatedAt gösterimi
- Menü kategori öncelik sıralaması:
  Kebaplar > Izgaralar > Dönerler > Pideler > Lahmacunlar > Burgerler > Pizzalar > Çorbalar > Salatalar > Ara Sıcaklar > Tatlılar > İçecekler

## Kritik Dosyalar

### Backend
- `restaurant/Restaurant.java`
- `restaurant/RestaurantController.java`
- `restaurant/AdminRestaurantController.java`
- `restaurant/CreateRestaurantRequest.java`
- `restaurant/UpdateRestaurantRequest.java`
- `menu/MenuItem.java`
- `menu/MenuController.java`
- `menu/AdminMenuController.java`
- `menu/CreateMenuItemRequest.java`
- `menu/UpdateMenuItemRequest.java`
- `menu/MenuOrderingUtil.java`
- `submission/MenuSubmission.java`
- `submission/SubmissionController.java`
- `submission/AdminSubmissionController.java`
- `auth/AuthController.java`
- `auth/JwtService.java`
- `auth/LoginRequest.java`
- `auth/LoginResponse.java`
- `config/SecurityConfig.java`

### Frontend
- `app/page.tsx`
- `app/login/page.tsx`
- `app/restaurants/[id]/page.tsx`
- `app/restaurants/[id]/submission-form.tsx`
- `app/admin/submissions/page.tsx`
- `app/admin/submissions/actions.tsx`
- `app/admin/restaurants/page.tsx`
- `app/admin/restaurants/new/page.tsx`
- `app/admin/restaurants/[id]/edit/page.tsx`
- `app/admin/restaurants/[id]/menu/page.tsx`
- `app/globals.css`
- `lib/api.ts`

## Auth
- Login endpoint: `/api/auth/login`
- Admin sayfaları JWT ile korunuyor
- Admin token localStorage içinde tutuluyor
- Admin kullanıcı adı: `admin`
- Admin şifre: `admin123`
- Bu bilgi production için değiştirilmeli

## Soft Delete Mantığı
- Restoran entity’sinde `deleted` ve `deletedAt` alanları var
- Public endpoint’lerde deleted restoranlar filtreleniyor
- Admin endpoint’lerde hepsi görünüyor
- Admin listede silinmiş restoran icon ile gösteriliyor

## Notlar
- Bir ara BOM / UTF-8 sorunları yaşandı
- Maven local repo path sorunu yaşandı
- IntelliJ üzerinden run etmek stabil çalıştı
- GitHub repo: `https://github.com/berkayarslan/menuhub`

## Sıradaki Muhtemel Geliştirmeler
- Menü ürünleri için soft delete
- Menü ürünü edit modal / inline edit
- Admin panelinde restoran arama/filtre
- OCR upload akışı
- Submission review + edit before approve
- Admin dışı kullanıcı sistemi
- Restaurant owner rolü