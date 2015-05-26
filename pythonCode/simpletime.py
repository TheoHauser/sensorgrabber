import datetime
import os

path = "/home/reu/HealthAppVideos/"

def read_files(path):
  files = []
  for name in os.listdir(path)
    if os.path.isfile(os.path.join(path, name)):
      files.append(name)
  return files

def choose_file(uFiles):
  print("Options: ")
  filecount = 0
  for filecount in range(0, len(uFiles)):
    print(str(filecount) + ". " + str(uFiles[filecount])
  index = input("Enter choice number: ")
  return uFiles[index]

def file_to_datetime(filelist):
  datetime_string = (videoname[4:8] + "/" + videoname[8:10] + "/" + videoname[10:12] + " "
                  + videoname[13:15] + ":" + videoname[15:17] + ":" + videoname[17:19])
  return datetime.datetime.strptime(datetime_string, "%Y/%m%d %H:%M:%S")

filelist = read_files(path)
while True:
  videoname = choose_file(filelist)
  dts = file_to_datetime(videoname)
  loop = True
  while loop:
    userprompt = raw_input("Enter occurance or 'Q' to quit: ")
    if (userprompt == 'q' or userprompt == 'Q'):
      loop = False
    else:
      returntime = dts + datetime.timedelta(seconds=int(userprompt))
    print returntime
