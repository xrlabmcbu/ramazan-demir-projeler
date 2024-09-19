import tkinter as tk
import numpy as np
import tensorflow as tf
import joblib
import catboost
import xgboost as xgb
import http.client
import json
from tkinter import PhotoImage

lstm_model = tf.keras.models.load_model("models/lstm_model.h5")
gradient_boosting_model = joblib.load("models/gradient_boosting_model.pkl")
catboost_model = catboost.CatBoostRegressor()
catboost_model.load_model("models/catboost_model.cbm")
xgb_model = xgb.XGBRegressor()
xgb_model.load_model("models/xgb_model.json")

def get_temperature(city):
    conn = http.client.HTTPSConnection("api.collectapi.com")  #baglantı kurma

    headers = {  #başlıkları ayarlama  ek bilgi saglamak    
        'content-type': "application/json",  #veri formatı belirler 
        'authorization': "apikey 1eCTylXF9dCp9jyQhv0i5j:1ckjy27Ja3JM4tMpfP1uPN"
    }

    conn.request("GET", f"/weather/getWeather?data.lang=tr&data.city={city}", headers=headers) #istek gönderme 
 
    res = conn.getresponse()  #cevabı alıyor 
    data = res.read()  #veri okuma 
    weather_data = json.loads(data.decode("utf-8"))
    
    # Sıcaklık bilgisini al
    temperature = weather_data['result'][0]['degree']  #sıcaklık bilgisi alma 
    return float(temperature)

def submit_values():
    try:
        # Kullanıcıdan değerleri al
        city = city_var.get()
        wind_speed = float(wind_speed_var.get())
        wind_direction = float(wind_direction_var.get())
        pitch_angle = float(pitch_angle_var.get())

        # Şehre göre sıcaklık değerini al
        temperature = get_temperature(city)

        # Girdi verisini hazırlayın (modelin beklediği formata göre)
        input_data = np.array([[wind_speed, wind_direction, pitch_angle, temperature]])

        # Tahminleri yap
        lstm_predictions = lstm_model.predict(input_data)
        gb_predictions = gradient_boosting_model.predict(input_data)
        catboost_predictions = catboost_model.predict(input_data)
        xgb_predictions = xgb_model.predict(input_data)

        # Sonuçları ekranda göster
        result_label.config(
            text=(
                f"{city} şehrindeki sıcaklık: {temperature}°C\n\n"
                f"R² Score: 0.95 LSTM -> {lstm_predictions[0][0]}\n"
                f"R² Score: 0.98 GB -> {gb_predictions[0]}\n"
                f"R² Score: 0.99 CatBoost -> {catboost_predictions[0]}\n"
                f"R² Score: 0.97 XGBoost -> {xgb_predictions[0]}"
            ),
            font=("Helvetica", 10),
            anchor="w",
            justify="left"
        )
    except ValueError:
        result_label.config(text="Invalid input. Please enter valid numerical values.")

root = tk.Tk()
root.title("Wind Power")
root.geometry("400x600")
root.configure(bg='white')

root.eval('tk::PlaceWindow . center')

photo_image = PhotoImage(file="wind-power.png")

photo_label = tk.Label(root, image=photo_image, bg='white')
photo_label.pack(pady=(0, 10))

city_var = tk.StringVar()
wind_speed_var = tk.StringVar()
wind_direction_var = tk.StringVar()
pitch_angle_var = tk.StringVar()

tk.Label(root, text="City:", bg='white').pack(pady=(20, 5))
tk.Entry(root, textvariable=city_var).pack(pady=5)

tk.Label(root, text="Wind Speed (m/s):", bg='white').pack(pady=(20, 5))
tk.Entry(root, textvariable=wind_speed_var).pack(pady=5)

tk.Label(root, text="Wind Direction (degrees):", bg='white').pack(pady=5)
tk.Entry(root, textvariable=wind_direction_var).pack(pady=5)

tk.Label(root, text="Pitch Angle (degrees):", bg='white').pack(pady=5)
tk.Entry(root, textvariable=pitch_angle_var).pack(pady=5)

submit_button = tk.Button(root, text="Submit", command=submit_values, bg='white')
submit_button.pack(pady=10)

result_label = tk.Label(root, text="", bg='white')
result_label.pack(pady=10)

root.bind("<Escape>", lambda e: root.destroy())

root.mainloop()
