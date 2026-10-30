# Run from the root directory of your Android project.
# Uses the ISO codes in arrays_with_iso_codes.xml, preserving array order.
from pathlib import Path
import urllib.request
import xml.etree.ElementTree as ET

xml_path = Path("app/src/main/res/values/strings.xml")
drawable_dir = Path("app/src/main/res/drawable")
drawable_dir.mkdir(parents=True, exist_ok=True)
root = ET.parse(xml_path).getroot()
codes = [item.text for item in root.find("string-array[@name='country_iso_codes']")]
for code in codes:
    output = drawable_dir / f"flag_{code}.png"
    if output.exists():
        continue
    try:
        urllib.request.urlretrieve(f"https://flagcdn.com/w80/{code}.png", output)
        print("Downloaded", output.name)
    except Exception as exc:
        print("Failed", code, exc)
        output.unlink(missing_ok=True)

