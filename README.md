# Java Monitor Producer-Consumer

Bu proje Java kullanılarak Producer–Consumer probleminin
monitor yapısı ile simülasyonunu içermektedir.

## Proje Özellikleri
- Çoklu producer ve consumer thread yapısı
- HIGH ve LOW öncelikli buffer kullanımı
- synchronized, wait() ve notifyAll() ile senkronizasyon
- Poison Pill yöntemi ile güvenli consumer kapanışı

## Kullanılan Yapılar
- Java Thread & Runnable
- Monitor yaklaşımı
- Producer Consumer Design Pattern

## Çalışma Mantığı
- 3 Producer thread, her biri 50 sayı üretir
- Üretilen sayılar önceliklerine göre bufferlara eklenir
- Consumer’lar öncelikle HIGH buffer’dan tüketir
- Üretim tamamlanınca consumer’lar düzgün şekilde sonlandırılır

## Çıktılar
- Konsol üzerinden işlem logları
- Program sonunda özet rapor
