# -*- coding: utf-8 -*-

import subprocess
import csv
import sys
import os
#from pprint import pprint

def download_scene(scene, output_directory):
  '''
  Take all the scenes and download it by storing it in output directory.
  '''
  subprocesses = []
  files = []

  files_L5 = ('B1', 'B2', 'B3', 'B4', 'B5', 'B6', 'B7', 'BQA', 'MTL')
  files_L7 = ('B1', 'B2', 'B3', 'B4', 'B5', 'B6_VCID_1', 'B6_VCID_2', 'B7', 'B8', 'BQA', 'MTL')
  files_L8 = ('B1', 'B2', 'B3', 'B4', 'B5', 'B6', 'B7', 'B8', 'B9', 'B10', 'B11', 'BQA', 'MTL')

  if scene['sensor_number'] == '8': files = files_L8
  elif scene['sensor_number'] == '7': files = files_L7
  else: files = files_L5
  
  for fl in files:
    new_url = ''
    product_band_id = ''

    if fl == 'MTL':
      product_band_id = scene['product_id'] + '_' + fl + '.txt'
      new_url = scene['base_download_url'] + '_' + fl + '.txt'
    else:
      product_band_id = scene['product_id'] + '_' + fl + '.TIF'
      new_url = scene['base_download_url'] + '_' + fl + '.TIF'

    FNULL = open(os.devnull, 'w')
    subprocesses.append((subprocess.Popen(' '.join(['curl', new_url, '--output', output_directory + '/' + product_band_id]), shell=True, stdout=FNULL, stderr=FNULL), scene['scene_id'] + '_' + fl))
  for (process, scene_id) in subprocesses:
    process.wait()

def get_processed_at_from_scene_id(scene_id):
  '''
  Take year and julian day of scene id.
  '''
  return scene_id[9:16]

def build_product_obj(scene_id, date, download_url):
  '''
  Builds a scene product and returns.
  '''
  product_obj = {}
  product_obj['scene_id'] = scene_id
  product_obj['product_id'] = download_url.split('/')[-1]
  product_obj['sensor_number'] = scene_id[2]
  product_obj['base_download_url'] = download_url + '/' + product_obj['product_id']
  product_obj['download_url'] = download_url
  product_obj['processed_at'] = get_processed_at_from_scene_id(scene_id)
  
  return product_obj

def is_valid_product(scene_id, date, dataset):
  '''
  Checks whether the scene ID is an L5 or L7 image
  and belongs to the respective start and end intervals.
  '''

  is_valid = True
  processed_at = get_processed_at_from_scene_id(scene_id)
  if date and processed_at != date:
    is_valid = False
  if scene_id[2] != dataset:
    is_valid = False

  return is_valid

def search(path, row, date, dataset):
  '''
  Take each product in the scenes list, store everything
  with path/row and date range and return.
  '''
  products = []
  try:
    with open('scene_list') as csvfile:
      csv_reader = csv.reader(csvfile, delimiter = ',')
      
      header = csv_reader.next()
      
      path_index = header.index('PATH')
      row_index = header.index('ROW')
      scene_id_index = header.index('SCENE_ID')
      download_url_index = header.index('DOWNLOAD_URL')
      cloud_cover_index = header.index('CLOUD_COVER')

      for product in csv_reader:
        scene_id = product[scene_id_index]
        if is_valid_product(scene_id, date, dataset):
          download_url = product[download_url_index]
          product_obj = build_product_obj(scene_id, date, download_url)
          products.append(product_obj)

  except IOError:
    print "Scene_list file not found. Try run 'python main.py setup' first."
    raise SystemExit

  return products

def setup(ulx, uly, brx, bry):
  '''
  Download all informations used in main execution.
  '''
  subprocess.call(['sh', 'download_scenes.sh', str(ulx), str(uly), str(brx), str(bry)])

def format(julian_day):
  tam = len(str(julian_day))
  if tam == 1: return '00' + str(julian_day)
  elif tam == 2: return '0' + str(julian_day)
  else: return str(julian_day)

def check_year(year):
  if (year % 400 == 0):
    return True
  if (year % 100 == 0):  
    return False
  if (year % 4 == 0):
    return True
  return False

def get_julian_day(date):
  old_leap_year = 1960
  
  #months_leap_year=(31, 29, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31)
  months_year=(31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31)
  
  year, month, day = map(int, date.split('-'))
  julian_day = 0
  for i in xrange(month-1):
    if(i == 1 and check_year(year)):
      julian_day += 1
    julian_day += months_year[i]
  julian_day += day

  return format(julian_day)

if __name__ == '__main__':
  if len(sys.argv) == 5:
    dataset, path_row, date, results = sys.argv[1:]

    path = path_row[:-3]
    row = path_row[4:]
    julian_day = get_julian_day(date)
    date_f = str(year) + julian_day
    dataset = dataset[-1]

    setup(path, row, path, row)
    scenes_candidates = search(path, row, date_f, dataset)
    if len(scenes_candidates):
      download_scene(scenes_candidates[0], results)
    else:
      sys.exit(3)
