from datetime import datetime
import matplotlib.pyplot as plt
import numpy as np

#Savitzky-Golay Filter
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

data = np.genfromtxt('/home/sam/SensorGrabber/Data/06-16-2015 14:57:58.csv', delimiter=',', names=['time', 'sec', 'azi'])

x = datetime.strptime(str(data['time']), '%H:%M:%S') #FIX THIS
y = data['azi']
ynew = savitzky_golay(y, 3, 1)

plt.figure(1)
plt.plot(x, ynew, '-', x, y, 'o')
plt.yticks(range(-4, 4, 1))
plt.show()

#noFilter
#plt.figure(1)
#plt.plot(data['sec'], (((np.floor(data['azi'])+np.ceil(data['azi']))/2)), label='azimuth')
#plt.plot(data['sec'], data['azi'], label='azimuth')
#plt.show()
