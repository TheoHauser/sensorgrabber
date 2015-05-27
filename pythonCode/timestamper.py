import datetime
import time
from PIL import Image, ImageDraw, ImageFont
import moviepy.editor as mp
from moviepy.video.io.bindings import PIL_to_npimage

#TODO: Ability to specify path name and file name

path = "/home/reu/HealthAppVideos/"
videoname = "VID_20150519_102443.mp4"

datetime_string = videoname[4:8] + "/" + videoname[8:10] + "/" + videoname[10:12] + " " + videoname[13:15] + ":" + videoname[15:17] + ":" + videoname[17:19]
dts = datetime.datetime.strptime(datetime_string, "%Y/%m/%d %H:%M:%S")
runtime = time.time()
nowtime = 0

fontname = "/usr/share/fonts/truetype/freefont/FreeMono.ttf"
font = ImageFont.FreeTypeFont(fontname, 36)

def timestamp_filter(video_frame, t):
	printtime = dts + datetime.timedelta(seconds=t)
	im = Image.fromarray(video_frame(t))
	draw = ImageDraw.Draw(im)
	draw.text((2, 2), str(printtime), font=font)
	return PIL_to_npimage(im)

clip = mp.VideoFileClip(path+videoname)
timestamped_clip =clip.fl(timestamp_filter)
timestamped_clip.write_videofile(path + "TIMESTAMPED_" + videoname, bitrate='8000k')
