from flask import Flask, render_template, request, jsonify, Response
import cv2
from ultralytics import YOLO
import os

app = Flask(__name__)

# YOLO modelini yükle
model = YOLO("best.pt")

# Global kamera durumu
camera_active = False
cap = None

@app.route('/')
def index():
    return render_template('index.html')

@app.route('/camera')
def camera():
    return render_template('camera.html')

def generate_frames():
    global cap
    while camera_active:
        success, frame = cap.read()
        if not success:
            break
        else:
            # Modeli kullanarak tahmin yapv
            results = model(frame, conf=0.45)

            # Sonuçları işleme
            for r in results:
                annotated_frame = r.plot()

            # Kareyi işleyin ve JPEG formatına çevirin
            ret, buffer = cv2.imencode('.jpg', annotated_frame)
            frame = buffer.tobytes()
            yield (b'--frame\r\n'
                   b'Content-Type: image/jpeg\r\n\r\n' + frame + b'\r\n')
    cap.release()

@app.route('/start_camera')
def start_camera():
    global camera_active, cap
    if not camera_active:
        cap = cv2.VideoCapture(0)
        if not cap.isOpened():
            return jsonify({'message': 'Webcam açılamadı.'}), 500
        camera_active = True
    return jsonify({'message': 'Kamera başlatıldı.'})

@app.route('/stop_camera')
def stop_camera():
    global camera_active, cap
    if camera_active:
        camera_active = False
        if cap is not None:
            cap.release()
    return jsonify({'message': 'Kamera durduruldu.'})

@app.route('/video_feed')
def video_feed():
    return Response(generate_frames(), mimetype='multipart/x-mixed-replace; boundary=frame')

@app.route('/upload', methods=['POST'])
def upload_file():
    if 'file' not in request.files:
        return jsonify({'error': 'Dosya yüklenmedi.'}), 400

    file = request.files['file']
    if file.filename == '':
        return jsonify({'error': 'Dosya seçilmedi.'}), 400
    
    if file and file.filename.lower().endswith(('.png', '.jpg', '.jpeg')):
        # Fotoğrafı geçici bir dosyaya kaydedin
        filepath = os.path.join('static', file.filename)
        file.save(filepath)

        # Fotoğrafı modelle analiz et
        img = cv2.imread(filepath)
        results = model(img, conf=0.25)
        annotated_frame = None

        for r in results:
            annotated_frame = r.plot()

        # Sonucu JPEG formatında dönüştür
        ret, buffer = cv2.imencode('.jpg', annotated_frame)
        frame = buffer.tobytes()
        result_image_path = os.path.join('static', 'result.jpg')
        cv2.imwrite(result_image_path, annotated_frame)

        return render_template('result.html', image_url=result_image_path)
    else:
        return jsonify({'error': 'Geçersiz dosya formatı.'}), 400

if __name__ == '__main__':
    app.run(debug=True)
