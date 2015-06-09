filename = '/home/hauser2016/Project/sensorgrabber/matlabCode/CSV Data/06-04-2015 14:38:58.csv';
M = csvread(filename);
time = M(:,2);
azimuth = M(:,3);
pitch = M(:,4);
roll = M(:,5);

%converting from rad to deg
azi = rad2deg(azimuth);
len = length(azi)-1;
for i=1:len
    if(azi(i)<0)
        azi(i) = azi(i)+360;
    end
end
%pit = rad2deg(pitch);
%rol = rad2deg(roll);

%smoothing
azis = smooth(azi,0.3,'rloess');
%pits = smooth(pit,0.3,'rloess');
%rols = smooth(rol,0.3,'rloess');

%unfiltered plot
figure(1);
plot(time,azi,'-O');
axis([0 inf -10 370]); 

hold on

%smooth plot
figure(2);
plot(time,azis,'-O');
axis([0 inf -10 370]);
