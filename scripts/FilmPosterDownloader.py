import requests
import os
import json
import random
import re

# Configurazione
api_key = ""

# Percorsi relativi
base_resources_path = "../src/main/resources/static"
json_output_dir = os.path.join(os.path.dirname(__file__), base_resources_path, "json")
images_output_dir = os.path.join(os.path.dirname(__file__), base_resources_path, "image/multimedia")

# Crea le cartelle se non esistono
os.makedirs(json_output_dir, exist_ok=True)
os.makedirs(images_output_dir, exist_ok=True)

json_filename = "catalogo_film.json"
movies_list = []

def sanitize_filename(title):
    # Rimuove tutto ciò che non è alfanumerico, spazio, trattino o underscore
    # Poi sostituisce spazi con underscore
    clean_title = re.sub(r'[^\w\s-]', '', title)
    return clean_title.replace(" ", "_")

print("Inizio download catalogo e immagini (200 film)...")

try:
    for page in range(1, 11): # 10 pagine = 200 film
        url = f"https://api.themoviedb.org/3/movie/top_rated?api_key={api_key}&language=it-IT&page={page}"

        response = requests.get(url)
        response.raise_for_status()
        data = response.json()

        for movie in data.get('results', []):
            if movie.get('poster_path') and movie.get('title'):
                title = movie['title']
                poster_path_remote = movie['poster_path']

                # Nome file locale
                filename = f"{sanitize_filename(title)}.jpg"
                local_image_path = os.path.join(images_output_dir, filename)
                web_image_path = f"/image/Multimedia/{filename}" # Percorso per il browser

                # Scarica l'immagine se non esiste già
                if not os.path.exists(local_image_path):
                    img_url = f"https://image.tmdb.org/t/p/w500{poster_path_remote}"
                    img_data = requests.get(img_url).content
                    with open(local_image_path, 'wb') as handler:
                        handler.write(img_data)
                    # print(f"Scaricato: {filename}")

                movies_list.append({
                    "tmdb_id": movie['id'],
                    "titolo": title,
                    "prezzo": round(random.uniform(2.99, 5.99), 2),
                    "poster_url": web_image_path,
                    "descrizione": movie.get('overview', ''),
                    "voto": movie.get('vote_average', 0)
                })

        print(f"Pagina {page}/10 completata.")

    # Salva il JSON
    output_path = os.path.join(json_output_dir, json_filename)
    with open(output_path, 'w', encoding='utf-8') as f:
        json.dump(movies_list, f, ensure_ascii=False, indent=4)

    print(f"\nSuccesso! JSON salvato in: {output_path}")
    print(f"Immagini salvate in: {images_output_dir}")
    print(f"Totale film: {len(movies_list)}")

except Exception as e:
    print(f"\nErrore durante l'esecuzione: {e}")
