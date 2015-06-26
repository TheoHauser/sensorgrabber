from math import factorial
import matplotlib
import matplotlib.pyplot as plt
from matplotlib.dates import DateFormatter, MinuteLocator
import numpy as np
import datetime

#Savitzky-Golay Filter
#http://wiki.scipy.org/Cookbook/SavitzkyGolay
#https://en.wikipedia.org/w/index.php?title=Savitzky%E2%80%93Golay_filter
def savitzky_golay(y, window_size, order, deriv=0, rate=1):
	try:
		window_size = np.abs(np.int(window_size))
		order = np.abs(np.int(order))
	except ValueError, msg:
		raise ValueError('window_size and order have to be of type int')	
	if window_size % 2 != 1 or window_size < 1:
		raise TypeError('window_size must be a positive odd number')
	if window_size < order + 2:
		raise TypeError('windoe_size is too small for the polynomials order')
	order_range = range(order+1)
	half_window = (window_size - 1) // 2
	#precompute coefficients
	b = np.mat([[k**i for i in order_range] for k in range(-half_window, half_window+1)])
	m = np.linalg.pinv(b).A[deriv] * rate**deriv * factorial(deriv)
	# pad the signal at the extremes with
	# values taken from the signal itself
	firstvals = y[0] - np.abs( y[1:half_window+1][::-1] - y[0] )
	lastvals = y[-1] + np.abs(y[-half_window-1:-1][::-1] - y[-1])
	y = np.concatenate((firstvals, y, lastvals))
	return np.convolve( m[::-1], y, mode='valid')	

data = np.genfromtxt('/home/sam/SensorGrabber/Data/06-25-2015 15:55:38.csv', 
		      dtype=["|S13", int, float, float, float, float, float, float], delimiter=',', 
		      names=['time', 'sec', 'azi', 'pitch', 'roll', 'accx', 'accy', 'accz'])

x = data['sec']
y = data['azi']
acc_z = data['accz']
degree_y = np.array(np.degrees(y))
s_v_y = np.array(savitzky_golay(degree_y, 3, 1))
adjusted_acc = np.array(np.sqrt((data['accx']**2) + (data['accy']**2) + (data['accz']**2)))

#RAW DATA
plt.figure(1)
plt.plot(x, y, 'o')
ax = plt.gca()
ax.set_xticklabels(data['time']) #THIS STILL DOESN'T WORK
plt.locator_params(nbins=len(data)) #THIS TOO
plt.yticks(range(-4, 5, 1))
plt.xticks(rotation=70)
plt.show()

#RAW DATA CONVERTED TO -180 to +180
plt.figure(2)
plt.plot(x,degree_y, '-')
ax = plt.gca()
ax.set_xticklabels(data['time'])
plt.locator_params(nbins=len(data))
plt.xticks(rotation=70)
plt.show()

#CONVERTED AND FILTERED
plt.figure(3)
plt.plot(x, s_v_y, '-')
ax = plt.gca()
ax.set_xticklabels(data['time'])
plt.locator_params(nbins=len(data))
plt.xticks(rotation=70)
plt.show()

#ACCELERATION COMPARISON
plt.figure(4)
plt.plot(x, acc_z, '-',)
ax = plt.gca()
ax.set_xticklabels(data['time'])
plt.locator_params(nbins=len(data))
plt.xticks(rotation=70)
plt.show()
