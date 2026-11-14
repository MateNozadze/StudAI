import os
import time
import socket
import subprocess
from contextlib import contextmanager

def is_port_open(host: str = "127.0.0.1", port: int = 11434) -> bool:
    with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as s:
        s.settimeout(0.5)
        return s.connect_ex((host, port)) == 0

@contextmanager
def temporary_ollama_server():
    process = None
    started_by_us = False

    if not is_port_open():
        print("--- [Ollama] Starting Ollama for quiz generation... ---")
        creation_flags = subprocess.CREATE_NO_WINDOW if os.name == "nt" else 0
        # DEVNULL აუცილებელია, რომ Ollama-ს ლოგებმა ბუფერი არ გაჭედოს
        process = subprocess.Popen(
            ["ollama", "serve"],
            stdout=subprocess.DEVNULL,
            stderr=subprocess.DEVNULL,
            creationflags=creation_flags,
        )
        started_by_us = True

        # ველოდებით მაქსიმუმ 15 წამი, სანამ პორტი 11434 გაიხსნება
        for _ in range(120):
            if is_port_open():
                break
            time.sleep(0.5)
        else:
            if process:
                process.terminate()
            raise RuntimeError("Ollama server failed to start within 60 seconds.")

    try:
        yield
    finally:
        if started_by_us and process:
            print("--- [Ollama] Quiz generated. Shutting down Ollama to free VRAM/RAM... ---")
            if os.name == "nt":
                # Windows-ზე taskkill თიშავს როგორც ollama.exe-ს, ისე შვილობილ llama-server.exe პროცესს
                subprocess.run(
                    ["taskkill", "/F", "/T", "/PID", str(process.pid)],
                    stdout=subprocess.DEVNULL,
                    stderr=subprocess.DEVNULL,
                )
            else:
                process.terminate()