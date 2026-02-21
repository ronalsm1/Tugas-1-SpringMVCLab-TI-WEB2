Nama    : Ronald Saut Manurung <br>
NIM     : 2481022 <br>
Prodi   : Teknik Informatika

# Jawaban Week 4 Lab

## Latihan 1

### Eksperimen 1: @Controller vs @RestController
- /test/view → Ini dari @Controller
- /test/text → Ini dari @Controller + @ResponseBody → text langsung
- Apa perbedaannya? /test/view memproses dan merender file HTML, sedangkan /test/text memotong proses render template dan mengembalikan plain text langsung sebagai response (berperilaku seperti API).
- Kesimpulan: @Controller tanpa @ResponseBody → return nama template. Dengan @ResponseBody → return data langsung.

### Eksperimen 2: Template Tidak Ada
- Apakah berhasil? Tidak
- HTTP Status: (type=Internal Server Error, status=500)
- Error: Whitelabel Error Page. This application has no explicit mapping for /error, so you are seeing this as a fallback. <br>
  Error resolving template [product/halaman-tidak-ada], template might not exist or might not be accessible by any of the configured Template Resolvers.
- Kesimpulan: Jika Controller return nama view yang tidak ada, Spring akan mengembalikan error 500 karena Thymeleaf tidak dapat menemukan file HTML yang diminta di dalam folder templates/ saat proses render.

### Eksperimen 3: @RequestParam vs @PathVariable
| URL | Hasil |
|---|---|
| /greet | Selamat Pagi, Mahasiswa! |
| /greet?name=Budi | Selamat Pagi, Budi! |
| /greet?name=Budi&waktu=Siang | Selamat Siang, Budi! |
| /greet/Ani | Halo, Ani! |
| /greet/Ani/detail | Halo, Ani! |
| /greet/Ani/detail?lang=EN | Hello, Ani! |

- URL mana yang pakai `@RequestParam`? /greet, /greet?name=Budi, dan /greet?name=Budi&waktu=Siang
- URL mana yang pakai `@PathVariable`? /greet/Ani dan /greet/Ani/detail
- URL mana yang pakai keduanya? /greet/Ani/detail?lang=EN

### Pertanyaan Refleksi
1. Apa perbedaan antara @Controller dan @RestController? Dalam kasus apa kamu pakai masing-masing?
- Jawaban: @Controller mereturn nama view/template untuk merender halaman HTML, dipakai saat membangun aplikasi web konvensional (Full-stack Monolith) di mana frontend dan backend menyatu. @RestController mereturn data mentah (biasanya JSON) langsung ke response body, dipakai saat membuat layanan RESTful API untuk dikonsumsi oleh frontend terpisah (seperti React) atau aplikasi mobile.
2. Perhatikan bahwa template product/list.html dipakai oleh 3 endpoint berbeda (list all, filter by category, search). Apa keuntungannya membuat template yang reusable seperti ini?
- Jawaban: Mengikuti prinsip DRY (Don't Repeat Yourself). Keuntungannya adalah kemudahan pemeliharaan (maintenance). Jika ada perubahan desain pada layout atau tabel daftar produk, kita hanya perlu mengubah satu file list.html, dan perubahannya akan otomatis teraplikasikan ke fitur pencarian, filter, maupun daftar semua produk.
3. Kenapa Controller inject ProductService (bukan langsung akses data di ArrayList)? Apa yang terjadi kalau Controller langsung manage data?
- Jawaban: Ini penerapan prinsip Separation of Concerns (pemisahan tugas). Controller bertugas menangani request HTTP, sedangkan Service fokus pada logika bisnis dan pengolahan data. Jika Controller memanage data langsung, kodenya akan membengkak (bloated), sulit dirawat, dan menyulitkan proses Unit Testing di kemudian hari.
4. Apa perbedaan model.addAttribute("products", products) dengan return products langsung seperti di @RestController?
- Jawaban: model.addAttribute() menitipkan data ke dalam objek Model (wadah) yang nantinya dikirim ke Thymeleaf untuk disisipkan ke dalam tag HTML sebelum dikembalikan ke browser. Sedangkan return langsung pada @RestController akan mengonversi objek Java menjadi format JSON dan mengirimkannya mentah-mentah ke body response browser.
5. Jika kamu buka http://localhost:8080/products/abc (ID bukan angka), apa yang terjadi? Kenapa?
- Jawaban: Terjadi error 400 Bad Request. Hal ini terjadi karena ada Type Mismatch. Endpoint mengharapkan variabel {id} bertipe data Long (angka), namun kita mengirimkan teks ("abc") yang tidak bisa dikonversi (parsing) oleh Spring menjadi sebuah angka.
6. Apa keuntungan pakai @RequestMapping("/products") di level class dibanding menulis full path di setiap @GetMapping?
- Jawaban: Membuat kode lebih bersih dan terstruktur. Kita tidak perlu menulis /products berulang-ulang pada setiap method. Jika di masa depan URL utama berubah (misal menjadi /items), kita cukup menggantinya di satu tempat saja pada anotasi level class.
7. Dalam lab ini, kata "Model" muncul dalam beberapa konteks berbeda. Sebutkan minimal 2 arti yang berbeda dan jelaskan perbedaannya.
- Jawaban: 1) Model Object (Spring MVC): Antarmuka Model (org.springframework.ui.Model) yang menjadi wadah tempat Controller meletakkan data (key-value) untuk dikirim ke View/Template. 2) Model Layer (POJO): Kelas representasi data (seperti Product.java) yang berada di package model, yang merepresentasikan entitas bisnis dalam aplikasi.

## Latihan 2

### Eksperimen 1: Fragment Tidak Ada
- Apakah error? Ya
- Error message: Whitelabel Error Page. This application has no explicit mapping for /error, so you are seeing this as a fallback. <br>
  Error resolving fragment: "~{'fragments/layout' :: navbar-yang-salah}": template or fragment could not be resolved (template: "home" - line 13, col 6). <br>
  There was an unexpected error (type=Internal Server Error, status=500).
- Kesimpulan: Jika nama fragment salah, Thymeleaf akan melempar error 500 karena gagal menemukan dan mengompilasi potongan template yang dibutuhkan saat merender halaman.

### Eksperimen 2: Static Resource
- CSS masih bekerja? Ya
  Setelah coba path yang salah:
- Apakah halaman error? Tidak
- Apakah CSS diterapkan? Tidak
- Kesimpulan: th:href="@{}" lebih baik karena ia akan otomatis menyesuaikan path (context root) jika aplikasi tidak di-deploy pada root domain utama. Jika file CSS tidak ada, aplikasi Spring Boot tidak akan melempar error 500/crash (seperti jika template HTML hilang), melainkan browser hanya gagal mengambil file gaya tersebut (mendapat error 404 pada file CSS).

### Pertanyaan Refleksi
1. Apa keuntungan menggunakan Thymeleaf Fragment untuk navbar dan footer?
- Jawaban: Menghindari duplikasi kode HTML. Kita cukup mendefinisikan navbar atau footer di satu tempat. Saat ada perubahan (seperti menambah menu "About"), kita hanya perlu mengubah file fragment, dan efeknya akan langsung terlihat di seluruh halaman yang memanggil fragment tersebut.
2. Apa bedanya file di static/ dan templates/? Kenapa CSS ada di static/ bukan templates/?
- Jawaban: Folder templates/ berisi file HTML dinamis yang memerlukan pemrosesan server-side (oleh Thymeleaf) sebelum dikirim ke browser, dan file ini tidak bisa diakses langsung via URL. Folder static/ berisi aset mentah (CSS, JS, Image) yang dikirim apa adanya ke browser dan bersifat publik. CSS diletakkan di static/ karena murni statis dan tidak perlu diproses ulang oleh backend Spring Boot.
3. Apa yang dimaksud dengan th:replace dan bagaimana bedanya dengan th:insert?
- Jawaban: th:replace mengganti/menghapus tag pembungkus utamanya (host tag) dan menaruh isi dari fragment sepenuhnya. Sebaliknya, th:insert menyisipkan isi fragment ke dalam tag pembungkus tersebut (tanpa menghapus tag pembungkus aslinya).
4. Kenapa kita pakai @{} untuk URL di Thymeleaf, bukan langsung tulis path?
- Jawaban: Karena syntax @{} (Link URL Expression) bersifat context-aware. Jika suatu saat server dipasang dengan context path tertentu (misalnya aplikasi jalan di http://server/tokoku/), Thymeleaf otomatis akan menambahkan /tokoku di depan setiap URL sehingga link tidak rusak (broken link).
5. Perhatikan bahwa ProductController inject ProductService melalui Constructor Injection (konsep dari Week 3). Apa jadinya kalau Controller tidak pakai DI dan langsung new ProductService() di dalam Controller?
- Jawaban: Terjadi Tight Coupling (ketergantungan ketat). Kelas Controller secara kaku terikat dengan implementasi spesifik dari ProductService. Ini sangat menyulitkan di masa depan jika kita ingin mengubah implementasi Service, serta membuat Controller hampir tidak mungkin diuji secara independen menggunakan Unit Testing (karena komponen Service tidak bisa di-mock).