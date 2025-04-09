package org.example.project.common.enums

enum class StatusLaporan(val label: String, val user: String, val ketua: String) {
    REJECTED("Ditolak", "Laporan Anda ditolak oleh satgas. Silakan periksa kembali laporan Anda.", ""),
    VERIFIED("Diterima", "Laporan Anda telah diverifikasi dan diterima. Silakan mengajukan tanggal klarifikasi.", ""),
    DELETED("Dihapus", "", ""),
    DRAFT("Dibuat", "", "Laporan baru diterima. Silakan lakukan verifikasi laporan"),
    FORM1("Diundang", "", "Pelapor sudah mengajukan tanggal klarifikasi. Cek di sini"),
    TEAMED("Tim dibentuk", "Satgas telah membentuk tim untuk menangani laporan anda.", ""),
    FORM2("Form 2", "", ""),
    DONE("Selesai", "Laporan Anda telah selesai diproses. Terima kasih atas laporan Anda. Setiap suara memiliki kekuatan, jangan takut untuk bersuara!", "Laporan sudah selesai. Silakan lakukan penutupan laporan"),
}