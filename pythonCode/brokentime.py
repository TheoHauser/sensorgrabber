import datetime
import time
from PIL import Image, ImageDraw, ImageFont
import moviepy.editor as mp
from moviepy.video.io.bindings import PIL_to_npimage

#TODO: Ability to specify path name and file name
#TODO: Get the time to update per frame

path = "/home/reu/HealthAppVideos/"
videoname = "VID_20150519_102443.mp4"

datetime_string = videoname[4:8] + "/" + videoname[8:10] + "/" + videoname[10:12] + " " + videoname[13:15] + ":" + videoname[15:17] + ":" + videoname[17:19]
dts = datetime.datetime.strptime(datetime_string, "%Y/%m/%d %H:%M:%S")

fontname = "/usr/share/fonts/truetype/freefont/FreeMono.ttf"
font = ImageFont.FreeTypeFont(fontname, 36)

def add_timestamp(video_frame):	
	im = Image.fromarray(video_frame) # transforms the Numpy image into a PIL image
	draw = ImageDraw.Draw(im)
	draw.text((2, 2), str(dts), font=font)
	return PIL_to_npimage(im) # transforms the PIL image back to a Numpy image

clip = mp.VideoFileClip(path+videoname)
timestamped_clip =clip.fl_image(add_timestamp)
#dts += datetime.timedelta(seconds=1)
timestamped_clip.write_videofile(path + "TIMESTAMPED" + videoname, bitrate='8000k')
