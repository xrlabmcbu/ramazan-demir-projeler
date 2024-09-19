import tkinter as tk
from tkinter import ttk, messagebox
from tkinter.filedialog import askdirectory
from selenium import webdriver
from selenium.webdriver.common.by import By
from selenium.webdriver.edge.service import Service  # Edge için Service modülünü ekleyin
from selenium.webdriver.edge.options import Options  # Edge için Options modülünü ekleyin
import time
import csv
import requests
from PIL import Image
from io import BytesIO
import pandas as pd
import os
from PIL import Image, ImageTk

# Edge tarayıcı ayarları
edge_options = Options()
edge_options.add_argument("--headless")  # Tarayıcı penceresini açmadan çalışmasını sağlar
service = Service("msedgedriver.exe")  # Edgedriver'ın yolunu belirtin
driver = webdriver.Edge(service=service, options=edge_options)

def get_image_urls(search_query, num_images):
    search_url = f"https://duckduckgo.com/?q={search_query}&t=h_&iax=images&ia=images"
    
    driver.get(search_url)
    time.sleep(2)  # Sayfanın yüklenmesi için bekleyin

    # Resim URL'lerini toplamak için bir liste
    image_urls = []

    # Daha fazla sonuç yüklemek için sayfayı kaydırma
    while len(image_urls) < num_images:
        # Resimlerin bulunduğu elementleri bulma
        images = driver.find_elements(By.CSS_SELECTOR, "img")
        for image in images:
            src = image.get_attribute('src')
            if src and src.startswith('http'):
                if src not in image_urls:
                    image_urls.append(src)
                if len(image_urls) >= num_images:
                    break
        # Daha fazla resim yüklenmesi için kaydırma
        driver.execute_script("window.scrollBy(0, document.body.scrollHeight);")
        time.sleep(2)

    return image_urls

def save_to_csv(image_urls, filename):
    with open(filename, mode='w', newline='', encoding='utf-8') as file:
        writer = csv.writer(file)
        writer.writerow(["Image URL"])
        for url in image_urls:
            writer.writerow([url])

def download_image(url, folder_path, index):
    try:
        # URL'den resmi indirin
        response = requests.get(url)
        if response.status_code == 200:
            # Resmi belleğe yükleyin
            img = Image.open(BytesIO(response.content))

            img = img.resize((256, 256))
            
            # Resmi kaydedin
            file_path = os.path.join(folder_path, f'imageduck_{index}.jpg')
            img.save(file_path)
            print(f'Resim {index} başarıyla indirildi ve kaydedildi: {file_path}')
        else:
            print(f'Resim indirilemedi. HTTP Durum Kodu: {response.status_code} - URL: {url}')
    except Exception as e:
        print(f'Bir hata oluştu: {e} - URL: {url}')

def search_images():
    search_query = query_entry.get()
    try:
        num_images = int(num_images_entry.get())
    except ValueError:
        messagebox.showerror("Error", "Please enter a valid number for the number of images.")
        return

    if not search_query:
        messagebox.showerror("Error", "Please enter a search query.")
        return

    image_urls = get_image_urls(search_query, num_images)
    save_to_csv(image_urls, 'image_urls.csv')
    messagebox.showinfo("Success", "Image URLs saved to 'image_urls.csv'.")
    driver.quit()

def download_images():
    # CSV dosyasını pandas ile oku
    df = pd.read_csv('image_urls.csv')
    
    # Klasör seçim penceresi aç
    folder_path = askdirectory(title="Select Folder to Save Images")
    if not folder_path:
        messagebox.showerror("Error", "No folder selected.")
        return

    # Her URL'yi işle
    for idx, row in df.iterrows():
        url = row['Image URL']
        download_image(url, folder_path, idx + 1)
    
    messagebox.showinfo("Success", "Images downloaded successfully.")

# Tkinter GUI
root = tk.Tk()
root.title("Google Images Scraper")

image_path = "foto.png"  # Buraya fotoğrafın yolunu yaz
image = Image.open(image_path)
photo = ImageTk.PhotoImage(image)

# Bir etiket oluştur ve fotoğrafı yerleştir
label = tk.Label(root, image=photo)
label.pack(pady=30)

# Arama sorgusu için etiket ve giriş alanı
query_label = ttk.Label(root, text="Search Query:")
query_label.pack(padx=10, pady=5)
query_entry = ttk.Entry(root, width=30)
query_entry.pack(padx=10, pady=5)

# Resim sayısı için etiket ve giriş alanı
num_images_label = ttk.Label(root, text="Number of Images:")
num_images_label.pack(padx=10, pady=5)
num_images_entry = ttk.Entry(root, width=30)
num_images_entry.pack(padx=10, pady=5)

# Arama butonu
search_button = ttk.Button(root, text="Search", command=search_images, width=17)
search_button.pack(padx=10, pady=10)

# İndirme butonu
download_button = ttk.Button(root, text="Download Images", command=download_images, width=17)
download_button.pack(padx=10, pady=10)

root.geometry("800x500")  # Pencere boyutunu ayarla
root.mainloop()
