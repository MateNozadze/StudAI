import os
import time
import socket
import subprocess
from urllib.parse import urlparse
from contextlib import contextmanager

def get_ollama_host_port():
    """OLLAMA_BASE_URL-იდან გამოაქვს host და port, რომ Docker-შიც სწორად შემოწმდეს"""
    ollama_url = os.getenv("OLLAMA_BASE_URL", "http://127.0.0.1:11434")
    parsed = urlparse(ollama_url)
    host = parsed.hostname or "127.0.0.1"
    port = parsed.port or 11434
    return host, port

def is_port_open(host: str, port: int) -> bool:
    with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as s:
        s.settimeout(0.5)
        return s.connect_ex((host, port)) == 0

@contextmanager
def temporary_ollama_server():
    host, port = get_ollama_host_port()
    
    # თუ ვართ Docker-ში (ანუ host არ არის ლოკალური მანქანა), 
    # არ ვცდილობთ ollama-ს სერვერის თავად გაშვებას/ჩაქრობას (ის ჰოსტზეა მუშა მდგომარეობაში).
    is_local = host in ("127.0.0.1", "localhost", "host.docker.internal")
    
    process = None
    started_by_us = False

    if is_local and not is_port_open(host, port):
        print("--- [Ollama] Starting Ollama for quiz generation... ---")
        creation_flags = subprocess.CREATE_NO_WINDOW if os.name == "nt" else 0
        try:
            process = subprocess.Popen(
                ["ollama", "serve"],
                stdout=subprocess.DEVNULL,
                stderr=subprocess.DEVNULL,
                creationflags=creation_flags,
            )
            started_by_us = True

            # ველოდებით მაქსიმუმ 30 წამი
            for _ in range(60):
                if is_port_open(host, port):
                    break
                time.sleep(0.5)
            else:
                if process:
                    process.terminate()
                raise RuntimeError("Ollama server failed to start within 30 seconds.")
        except FileNotFoundError:
            print("--- [Ollama] Warning: 'ollama' executable not found locally. Skipping auto-start. ---")

    try:
        yield
    finally:
        if started_by_us and process:
            print("--- [Ollama] Quiz generated. Shutting down Ollama to free VRAM/RAM... ---")
            if os.name == "nt":
                subprocess.run(
                    ["taskkill", "/F", "/T", "/PID", str(process.pid)],
                    stdout=subprocess.DEVNULL,
                    stderr=subprocess.DEVNULL,
                )
            else:
                process.terminate()