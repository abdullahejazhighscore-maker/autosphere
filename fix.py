import sys

file_path = 'c:/Users/SamTech/OneDrive/Documents/cursor_carapp/src/main/java/com/samtech/carapp/HtmlRenderer.java'

with open(file_path, 'r', encoding='utf-8') as f:
    content = f.read()

old_str_1 = '''<img src="/logo.png" alt="Autosphere" height="40" class="brand-logo" style="margin-right:10px;"/>
                        <h1>Autosphere</h1>'''
new_str_1 = '''<img src="/logo.png" alt="Autosphere" class="brand-logo"/>'''

old_str_2 = '''<img src="/logo.png" alt="Autosphere" height="40" class="brand-logo" style="margin-right:10px;"/>
                        <h1>Autosphere Seller Panel</h1>'''
new_str_2 = '''<img src="/logo.png" alt="Autosphere" class="brand-logo"/>
                        <h2 style="margin-left: 10px; color: #64748b; font-size: 1.2rem;">Seller Panel</h2>'''

old_str_3 = '''<img src="/logo.png" alt="Autosphere" height="40" class="brand-logo" style="margin-right:10px;"/>
                        <h1>Autosphere Admin Panel</h1>'''
new_str_3 = '''<img src="/logo.png" alt="Autosphere" class="brand-logo"/>
                        <h2 style="margin-left: 10px; color: #64748b; font-size: 1.2rem;">Admin Panel</h2>'''

old_str_4 = '''<img src="/logo.png" alt="Autosphere" height="80" />'''
new_str_4 = '''<img src="/logo.png" alt="Autosphere" style="height: 80px; max-width: 100%; object-fit: contain;" />'''

content = content.replace(old_str_1, new_str_1)
content = content.replace(old_str_2, new_str_2)
content = content.replace(old_str_3, new_str_3)
content = content.replace(old_str_4, new_str_4)

with open(file_path, 'w', encoding='utf-8') as f:
    f.write(content)

print('Replaced successfully')
