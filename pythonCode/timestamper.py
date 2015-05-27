import datetime
import time
from PIL import Image, ImageDraw, ImageFont
import moviepy.editor as mp
from moviepy.video.io.bindings import PIL_to_npimage
import os

IN_PATH = "/home/reu/HealthAppVideos/"
OUT_PATH = "/home/reu/HealthAppVideos/Timestamped/"

fontname = "/usr/share/fonts/truetype/freefont/FreeMono.ttf"
font = ImageFont.FreeTypeFont(fontname, 36)

def load_folder(path):
	files = []
	for name in os.listdir(path):
		if os.path.isfile(os.path.join(path, name)):
			files.append(name)
	return files

def make_datetime_string(videoname):
	#Manually-defined substrings are okay because it's the same for all videos recorded on android
	datetime_string = (videoname[4:8] + "/" + videoname[8:10] + "/" + videoname[10:12] + " "
			+ videoname[13:15] + ":" + videoname[15:17] + ":" + videoname[17:19])
	dts = datetime.datetime.strptime(datetime_string, "%Y/%m/%d %H:%M:%S")
	return dts 

def timestamp_filter(video_frame, t):
	printtime = make_datetime_string(folder[i]) + datetime.timedelta(seconds=t)
	im = Image.fromarray(video_frame(t))
	draw = ImageDraw.Draw(im)
	draw.text((2, 2), str(printtime), font=font)
	return PIL_to_npimage(im)

folder = load_folder(IN_PATH)
for i in range(0, len(folder)):
	clip = mp.VideoFileClip(IN_PATH+folder[i])
	timestamped_clip = clip.fl(timestamp_filter)
	timestamped_clip.write_videofile(OUT_PATH + "TIMESTAMPED_" + folder[i], bitrate='8000k')
