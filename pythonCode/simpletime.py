import datetime

#videoname = "VID_20150519_102443.mp4"
videoname = raw_input("Enter filename (should be typical Android format, 'VID_YYYYMMDD_HHMMSS.mp4'): \n")

datetime_string = videoname[4:8] + "/" + videoname[8:10] + "/" + videoname[10:12] + " " + videoname[13:15] + ":" + videoname[15:17] + ":" + videoname[17:19]
dts = datetime.datetime.strptime(datetime_string, "%Y/%m/%d %H:%M:%S")

while True:
  userprompt = raw_input("Enter occurance: ")
  dts += datetime.timedelta(seconds=int(userprompt))
  print dts
