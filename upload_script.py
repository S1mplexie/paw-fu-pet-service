import socket
import subprocess
import sys
import time

HOST = '43.136.175.87'
USER = 'root'
PASSWORD = '2395@xxa'
SCRIPT_PATH = r'D:\xm-petService\demo1\deploy_update.sh'
REMOTE_PATH = '/opt/deploy_update.sh'

def read_file(path):
    with open(path, 'r', encoding='utf-8', newline='\n') as f:
        return f.read()

def send_command(sock, cmd, password):
    """通过SSH发送命令（简化版本）"""
    # 这个方法太复杂了，让我们使用subprocess和expect脚本
    pass

def main():
    print(f"读取脚本文件: {SCRIPT_PATH}")
    script_content = read_file(SCRIPT_PATH)
    
    print(f"\n连接服务器 {HOST}...")
    print(f"上传部署脚本到 {REMOTE_PATH}")
    
    # 创建expect脚本
    expect_script = f"""#!/usr/bin/expect -f
set timeout 30
spawn ssh {USER}@{HOST} "cat > {REMOTE_PATH} && chmod +x {REMOTE_PATH}"
expect {{
    "password:" {{ send "{PASSWORD}\\r" }}
    "Password:" {{ send "{PASSWORD}\\r" }}
}}
expect eof
"""
    
    expect_path = r'D:\xm-petService\demo1\temp_upload.exp'
    with open(expect_path, 'w', encoding='utf-8', newline='\n') as f:
        f.write(expect_script)
    
    print("\n请手动执行以下命令（需要输入密码）：")
    print(f"  密码: {PASSWORD}")
    print(f"\n方法1 - 使用scp上传：")
    print(f'  scp "{SCRIPT_PATH}" {USER}@{HOST}:{REMOTE_PATH}')
    print(f"\n方法2 - 使用ssh直接创建：")
    print(f'  ssh {USER}@{HOST} "cat > {REMOTE_PATH}" < "{SCRIPT_PATH}"')
    
    # 尝试使用PowerShell
    ps_script = f'''
$password = "{PASSWORD}"
$scriptPath = "{SCRIPT_PATH.replace(chr(92), chr(92)*2)}"
$remotePath = "{REMOTE_PATH}"
$host = "{HOST}"
$user = "{USER}"

# 读取脚本内容
$content = Get-Content $scriptPath -Raw

# 创建plink命令（如果可用）
Write-Host "尝试使用PowerShell上传..."
'''
    
    ps_path = r'D:\xm-petService\demo1\temp_upload.ps1'
    with open(ps_path, 'w', encoding='utf-8') as f:
        f.write(ps_script)
    
    # 直接尝试通过subprocess调用ssh，通过管道传递内容
    print("\n尝试通过SSH上传脚本内容...")
    
    try:
        # 使用ssh并在远程创建文件
        ssh_cmd = [
            'ssh',
            f'{USER}@{HOST}',
            f'cat > {REMOTE_PATH} && chmod +x {REMOTE_PATH}'
        ]
        
        # 启动进程
        proc = subprocess.Popen(
            ssh_cmd,
            stdin=subprocess.PIPE,
            stdout=subprocess.PIPE,
            stderr=subprocess.PIPE,
            text=True
        )
        
        # 发送脚本内容
        stdout, stderr = proc.communicate(input=script_content, timeout=30)
        
        if proc.returncode == 0:
            print("✓ 脚本上传成功！")
            print(f"远程路径: {REMOTE_PATH}")
            print(f"\n现在可以执行部署:")
            print(f"  ssh {USER}@{HOST} '{REMOTE_PATH}'")
        else:
            print(f"上传失败: {stderr}")
            
    except subprocess.TimeoutExpired:
        proc.kill()
        print("超时，可能需要密码认证")
    except Exception as e:
        print(f"错误: {e}")
        print("\n请手动执行以下命令：")
        print(f"  scp {SCRIPT_PATH} {USER}@{HOST}:{REMOTE_PATH}")
        print(f"  密码: {PASSWORD}")

if __name__ == '__main__':
    main()
