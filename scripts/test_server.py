#!/usr/bin/env python3
"""
本地测试服务器：监听 9000，接收长度前缀消息，解码 Base64(iv+ciphertext)，使用 AES-128-CBC/PKCS7 解密并打印 JSON。
依赖: pip install pycryptodome
"""
import socket
import struct
import base64
from Crypto.Cipher import AES

AES_KEY = b'0123456789abcdef'

def decrypt_from_base64(b64: str) -> str:
    raw = base64.b64decode(b64)
    iv = raw[:16]
    cipher_bytes = raw[16:]
    cipher = AES.new(AES_KEY, AES.MODE_CBC, iv)
    data = cipher.decrypt(cipher_bytes)
    pad = data[-1]
    return data[:-pad].decode('utf-8', errors='ignore')

def run(host='0.0.0.0', port=9000):
    s = socket.socket()
    s.bind((host, port))
    s.listen(1)
    print('listening on %s:%d' % (host, port))
    conn, addr = s.accept()
    print('connected', addr)
    try:
        while True:
            header = conn.recv(4)
            if not header:
                break
            length = struct.unpack('>I', header)[0]
            payload = b''
            while len(payload) < length:
                chunk = conn.recv(length - len(payload))
                if not chunk:
                    break
                payload += chunk
            try:
                base64_payload = payload.decode('utf-8')
                json = decrypt_from_base64(base64_payload)
            except Exception as e:
                json = payload.decode('utf-8', errors='ignore')
            print('RECV:', json)
    finally:
        conn.close()
        s.close()

if __name__ == '__main__':
    run()
